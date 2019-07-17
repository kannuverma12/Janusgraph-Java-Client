package com.paytm.digital.education.explore.kafka;

import com.paytm.digital.education.explore.kafka.model.KafkaConsumerState;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;

public interface KafkaConsumer extends ConsumerSeekAware {

    /**
     * Interface exposed method to listen to incoming kafka messages
     *
     * @param message
     * @param partition
     * @param offset
     */
    void listen(List<String> message, List<Integer> partition, List<Long> offset,
            Acknowledgment acknowledgment);

    /**
     * Interface exposed method to start kafka container from listening
     */
    void start();

    /**
     * Interface exposed method to stop kafka container
     */
    void stop();

    /**
     * Interface exposed method to configure the kafka container to seek a particular offset and
     * topic
     *
     * @param topic
     * @param partition
     * @param offset
     */
    void seek(String topic, int partition, long offset);

    /**
     * Interface exposed method to determine the kafka container is listening or not
     *
     * @return
     */
    boolean isListening();

    KafkaConsumerState getKafkaState();

}
