package com.paytm.digital.education.form.producer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String topic, String message) {
        log.info("KAFKA_PUBLISHER: TOPIC:{} MESSAGE:{}", topic, message);
        kafkaTemplate
                .send(topic, message)
                .addCallback(new TopicPublishCallback(topic, null, message));
    }

    public void sendMessage(String topic, String partitionKey, String message) {
        log.info("KAFKA_PUBLISHER TOPIC:{} KEY:{} MESSAGE:{}", topic, partitionKey, message);
        kafkaTemplate
                .send(topic, partitionKey, message)
                .addCallback(new TopicPublishCallback(topic, partitionKey, message));
    }

}
