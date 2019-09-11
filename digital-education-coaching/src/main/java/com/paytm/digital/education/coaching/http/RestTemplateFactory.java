package com.paytm.digital.education.coaching.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class RestTemplateFactory {

    @Autowired
    @Qualifier(HttpConstants.GENERIC_HTTP_SERVICE)
    private RestTemplate genericRestTemplate;

    private Map<String, RestTemplate> templateMap;

    @PostConstruct
    public void init() {
        templateMap = new HashMap<>();
        templateMap.put(HttpConstants.GENERIC_HTTP_SERVICE, genericRestTemplate);
    }

    public RestTemplate getRestTemplate(String serviceName) {
        if (!templateMap.containsKey(serviceName)) {
            log.error("RestTemplate doesn't exists for serviceName: {}", serviceName);
            return null;
        }
        return templateMap.get(serviceName);
    }

    public void addRestTemplate(String serviceName, RestTemplate restTemplate) {
        templateMap.put(serviceName, restTemplate);
    }
}
