package com.paytm.digital.education.explore.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.time.Duration;

@Configuration
@Data
public class RestConfig {

    private RestTemplate        restTemplate;
    private final RestTemplateBuilder restTemplateBuilder;
    private final long externalAPITimeoutInSeconds;

    public RestConfig(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${external.api.timeout.seconds}") long externalAPITimeoutInSeconds) {
        this.restTemplateBuilder = restTemplateBuilder;
        this.externalAPITimeoutInSeconds = externalAPITimeoutInSeconds;
    }

    @PostConstruct
    public void init() {
        restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(externalAPITimeoutInSeconds * 1000)).build();
    }

}
