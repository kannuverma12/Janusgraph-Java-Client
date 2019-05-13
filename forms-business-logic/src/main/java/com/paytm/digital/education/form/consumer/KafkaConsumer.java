package com.paytm.digital.education.form.consumer;

import com.paytm.digital.education.form.handler.BaseHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import static org.springframework.kafka.support.KafkaHeaders.RECEIVED_PARTITION_ID;
import static org.springframework.kafka.support.KafkaHeaders.RECEIVED_TOPIC;
import static org.springframework.kafka.support.KafkaHeaders.RECEIVED_TIMESTAMP;


@Service
@Slf4j
@Data
public class KafkaConsumer {

    @Qualifier("imageHandler")
    private final BaseHandler baseHandler;

    @KafkaListener(topics = "${app.topic.fillForm.images}")
    public void listen(@Payload String message,
                       @Header(RECEIVED_PARTITION_ID) int partition,
                       @Header(RECEIVED_TOPIC) String topic,
                       @Header(RECEIVED_TIMESTAMP) long ts) {
        log.info("Received Message {} {} {} {}", message, partition, topic, ts);
        baseHandler.processMessage(message);
    }
}

