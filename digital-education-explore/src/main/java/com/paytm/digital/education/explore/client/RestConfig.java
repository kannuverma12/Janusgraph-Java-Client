package com.paytm.digital.education.explore.client;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfig {

    private RestTemplate restTemplate;

    public RestTemplate getRestTemplate() {
        restTemplate = new RestTemplate();
        return restTemplate;
    }

}
