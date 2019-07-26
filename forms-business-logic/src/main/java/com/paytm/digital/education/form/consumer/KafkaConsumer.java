package com.paytm.digital.education.form.consumer;

import com.paytm.digital.education.form.handler.BaseHandler;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import static org.springframework.kafka.support.KafkaHeaders.RECEIVED_PARTITION_ID;
import static org.springframework.kafka.support.KafkaHeaders.RECEIVED_TOPIC;
import static org.springframework.kafka.support.KafkaHeaders.RECEIVED_TIMESTAMP;


@Slf4j
@Data
@Service
@ConditionalOnExpression("'${spring.profiles.active}' == 'production' or '${spring.profiles.active}' == 'local' "
        + "or '${kafka.consumer}' == T(java.net.InetAddress).getLocalHost().getHostName()")
@AllArgsConstructor
public class KafkaConsumer {

    @Qualifier("imageHandler")
    private final BaseHandler imageHandler;

    @Qualifier("notifyFulfilmentHandler")
    private final BaseHandler notifyFulfilmentHandler;

    @Qualifier("downloadOrderHandler")
    private final BaseHandler downloadOrderHandler;

    @KafkaListener(topics = "${app.topic.fillForm.images}")
    public void listenForImages(@Payload String message,
                       @Header(RECEIVED_PARTITION_ID) int partition,
                       @Header(RECEIVED_TOPIC) String topic,
                       @Header(RECEIVED_TIMESTAMP) long ts) {
        log.info("Received Message image - {} {} {} {}", message, partition, topic, ts);
        imageHandler.processMessage(message);
    }

    @KafkaListener(topics = "${app.topic.order.status.update}")
    public void listenForOrderStatusUpdates(@Payload String message,
                       @Header(RECEIVED_PARTITION_ID) int partition,
                       @Header(RECEIVED_TOPIC) String topic,
                       @Header(RECEIVED_TIMESTAMP) long ts) {
        log.debug("Received Message {} {} {} {}", message, partition, topic, ts);
        notifyFulfilmentHandler.processMessage(message);
    }

    @KafkaListener(topics = "${app.topic.order.filecenter}")
    public void listenForOrderIds(@Payload String message,
                       @Header(RECEIVED_PARTITION_ID) int partition,
                       @Header(RECEIVED_TOPIC) String topic,
                       @Header(RECEIVED_TIMESTAMP) long ts) {
        log.info("Received Message orderId - {} {} {} {}", message, partition, topic, ts);
        downloadOrderHandler.processMessage(message);
    }

}

