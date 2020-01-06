package com.paytm.digital.education.config;

import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

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

    @Value("${http.client.keep.alive.time:30000}")
    private Integer keepAliveInMillis;

    private static final Logger log = LoggerFactory.getLogger(RestApiConfiguration.class);

    @Bean
    public RestTemplate restTemplate() {
        return createRestTemplate();
    }

    private RestTemplate createRestTemplate() {
        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(getHttpClient());
        requestFactory.setReadTimeout(readTimeOut);
        RestTemplate template = new RestTemplate(requestFactory);
        List<HttpMessageConverter<?>> messageConverters = template.getMessageConverters();
        messageConverters.add(new FormHttpMessageConverter());
        template.setMessageConverters(messageConverters);
        return template;
    }

    private PoolingHttpClientConnectionManager getPoolingHttpClientConnectionManager() {
        PoolingHttpClientConnectionManager connectionManager =
                new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(poolMaxTotal);
        connectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
        connectionManager.setValidateAfterInactivity(httpConnectionValidateTime);
        return connectionManager;
    }

    private RequestConfig getRequestConfig() {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(connectionRequestTimeOut)
                .setConnectTimeout(connectionTimeOut)
                .setSocketTimeout(socketTimeOut)
                .build();
    }

    private CloseableHttpClient getHttpClient() {

        return HttpClientBuilder.create()
                .setConnectionManager(
                        getPoolingHttpClientConnectionManager())
                .evictExpiredConnections()
                .evictIdleConnections(30, TimeUnit.SECONDS)
                .setKeepAliveStrategy(getConnectionKeepAliveStrategy())
                .setDefaultRequestConfig(getRequestConfig())
                .build();
    }

    private ConnectionKeepAliveStrategy getConnectionKeepAliveStrategy() {
        return (response, context) -> {
            HeaderElementIterator it = new BasicHeaderElementIterator(
                    response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();
                if (value != null && param.equalsIgnoreCase("timeout")) {
                    try {
                        return Long.parseLong(value) * 1000;
                    } catch (NumberFormatException ex) {
                        log.error("Error in parsing keep-alive timeout, value = {} : ", ex, value);
                    }
                }
            }
            return keepAliveInMillis;
        };
    }
}
