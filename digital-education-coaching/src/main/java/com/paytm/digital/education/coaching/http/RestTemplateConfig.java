package com.paytm.digital.education.coaching.http;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Value("${generic.http.readTimeout}")
    private Integer genericReadTimeout;

    @Value("${generic.http.connectTimeout}")
    private Integer genericConnectionTimeout;

    @Bean(name = HttpConstants.GENERIC_HTTP_SERVICE)
    public RestTemplate genericRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(this.buildHttpRequestFactoryForRestTemplate(
                genericReadTimeout, genericConnectionTimeout));
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
        return restTemplate;
    }

    private HttpComponentsClientHttpRequestFactory buildHttpRequestFactoryForRestTemplate(
            final int readTimeout, final int connectTimeout) {
        final HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory =
                new HttpComponentsClientHttpRequestFactory();
        httpComponentsClientHttpRequestFactory.setReadTimeout(readTimeout);
        httpComponentsClientHttpRequestFactory.setConnectTimeout(connectTimeout);
        return httpComponentsClientHttpRequestFactory;
    }
}
