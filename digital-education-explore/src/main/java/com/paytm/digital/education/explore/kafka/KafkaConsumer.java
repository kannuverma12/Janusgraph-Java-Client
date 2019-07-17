package com.paytm.digital.education.explore.kafka;

import com.paytm.digital.education.explore.kafka.model.KafkaConsumerState;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;

public interface KafkaConsumer extends ConsumerSeekAware {

    void listen(List<String> message, List<Integer> partition, List<Long> offset,
            Acknowledgment acknowledgment);

    void start();

    void stop();

    void seek(String topic, int partition, long offset);

    boolean isListening();

    KafkaConsumerState getKafkaState();

}
