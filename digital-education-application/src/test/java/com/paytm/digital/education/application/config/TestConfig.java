package com.paytm.digital.education.application.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;

@TestConfiguration
public class TestConfig {

    @Bean
    public KafkaListenerEndpointRegistry getRegistry() {
        return new KafkaListenerEndpointRegistry();
    }
}
