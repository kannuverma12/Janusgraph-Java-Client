package com.paytm.digital.education.explore.kafka.impl;

import com.paytm.digital.education.explore.kafka.KafkaProducer;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

@Service
@AllArgsConstructor
public class KafkaProducerImpl implements KafkaProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;


    @Override
    public Future<SendResult<String, String>> sendMessage(String topic,
            String message) {
        return kafkaTemplate.send(topic, message);
    }

}
