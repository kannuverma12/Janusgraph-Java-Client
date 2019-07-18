package com.paytm.digital.education.explore.kafka.impl;

import com.paytm.digital.education.explore.es.model.SearchHistoryEsDoc;
import com.paytm.digital.education.explore.kafka.KafkaConsumer;
import com.paytm.digital.education.explore.kafka.model.KafkaConsumerState;
import com.paytm.digital.education.explore.service.RecentsSerivce;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class KafkaConsumerImpl implements KafkaConsumer {

    private ConsumerSeekAware.ConsumerSeekCallback consumerSeekCallback;
    private KafkaConsumerState                     kafkaConsumerState;

    @Autowired
    private RecentsSerivce recentsSerivce;

    @Value("${kafka.listener.topics}")
    private String kafkaTopic;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @KafkaListener(topics = "${kafka.listener.topics}")
    public void listen(@Payload List<String> messages,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
            @Header(KafkaHeaders.OFFSET) List<Long> offsets, Acknowledgment acknowledgment) {

        log.info("Read message {}", messages);
        try {
            List<SearchHistoryEsDoc> searchHistories = new ArrayList<>();
            for (String jsonStr : messages) {
                SearchHistoryEsDoc
                        searchHistoryEsDoc = JsonUtils.fromJson(jsonStr, SearchHistoryEsDoc.class);
                searchHistories.add(searchHistoryEsDoc);
            }
            acknowledgment.acknowledge();
            if (!CollectionUtils.isEmpty(searchHistories)) {
                recentsSerivce.ingestAudits(searchHistories);
            }

        } catch (Exception e) {
            log.error("Error in processing the msg. Can't ack kafka", e);
        }
    }

    @Override
    public void start() {
        kafkaListenerEndpointRegistry.start();

    }

    @Override
    public void stop() {
        kafkaListenerEndpointRegistry.stop();
    }

    @Override
    public void onIdleContainer(Map<TopicPartition, Long> map,
            ConsumerSeekAware.ConsumerSeekCallback callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPartitionsAssigned(Map<org.apache.kafka.common.TopicPartition, Long> map,
            ConsumerSeekAware.ConsumerSeekCallback callback) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.kafka.listener.ConsumerSeekAware#registerSeekCallback(org.springframework
     * .kafka.listener.ConsumerSeekAware.ConsumerSeekCallback)
     */
    @Override
    public void registerSeekCallback(ConsumerSeekAware.ConsumerSeekCallback callback) {
        this.consumerSeekCallback = callback;
    }

    @Override
    public void seek(String topic, int partition, long offset) {
        getKafkaState().setTopic(topic);
        getKafkaState().setOffset(offset);
        getKafkaState().setPartition(partition);

        this.consumerSeekCallback.seek(topic, partition, offset);
    }

    @Override
    public boolean isListening() {
        return kafkaListenerEndpointRegistry.isRunning();
    }

    @Override
    public KafkaConsumerState getKafkaState() {
        if (kafkaConsumerState == null) {
            kafkaConsumerState = KafkaConsumerState.builder().build();
            kafkaConsumerState.setTopic(kafkaTopic);
            kafkaConsumerState.setOffset(0);
            kafkaConsumerState.setPartition(0);
        }
        return kafkaConsumerState;
    }



}
