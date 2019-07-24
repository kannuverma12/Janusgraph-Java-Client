package com.paytm.digital.education.explore.kafka;

import org.springframework.kafka.support.SendResult;

import java.util.concurrent.Future;

public interface KafkaProducer {

    public Future<SendResult<String, String>> sendMessage(String topic,
            String message);

}
