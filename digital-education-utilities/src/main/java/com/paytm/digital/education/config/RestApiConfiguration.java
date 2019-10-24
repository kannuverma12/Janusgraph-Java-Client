package com.paytm.digital.education.config;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class RestApiConfiguration {

    @Value("${http.client.connection.max.pool.size:100}")
    private Integer poolMaxTotal;

    @Value("${http.client.connection.request.timeout:2000}")
    private Integer connectionRequestTimeOut;

    @Value("${http.client.connection.timeout:2000}")
    private Integer connectionTimeOut;

    @Value("${http.client.socket.timeout:25000}")
    private Integer socketTimeOut;

    @Value("${http.client.read.timeout:25000}")
    private Integer readTimeOut;

    @Value("${http.client.default.max.per.route:10}")
    private Integer defaultMaxPerRoute;

    @Value("${http.client.validate.inactivity.time:2000}")
    private Integer httpConnectionValidateTime;

    @Bean
    public RestTemplate restTemplate() {
        return createRestTemplate();
    }

    private RestTemplate createRestTemplate() {
        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient());
        requestFactory.setReadTimeout(readTimeOut);
        RestTemplate template = new RestTemplate(requestFactory);
        List<HttpMessageConverter<?>> messageConverters = template.getMessageConverters();
        messageConverters.add(new FormHttpMessageConverter());
        template.setMessageConverters(messageConverters);
        return template;
    }

    private PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        PoolingHttpClientConnectionManager connectionManager =
                new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(poolMaxTotal);
        connectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
        connectionManager.setValidateAfterInactivity(2000);
        return connectionManager;
    }

    private RequestConfig requestConfig() {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(connectionRequestTimeOut)
                .setConnectTimeout(connectionTimeOut)
                .setSocketTimeout(socketTimeOut)
                .build();
    }

    private CloseableHttpClient httpClient() {

        return HttpClientBuilder.create()
                .setConnectionManager(
                        poolingHttpClientConnectionManager())
                .setDefaultRequestConfig(requestConfig())
                .build();
    }
}
