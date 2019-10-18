package com.paytm.digital.education.coaching.connections.rest;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.MDC;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.PAYTM_REQUEST_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.RestTemplateConstants.HTTP_MAX_CONNECTION_ALLOWED;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.RestTemplateConstants.HTTP_MAX_CONNECTION_PER_REQUEST;

@Slf4j
public class CoachingRestTemplate extends RestTemplate {

    CoachingRestTemplate(HttpComponentsClientHttpRequestFactory httpRequestFactory) {
        super(httpRequestFactory);
    }

    CoachingRestTemplate() {
        super(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
    }

    CoachingRestTemplate(int timeout) {
        super(new BufferingClientHttpRequestFactory(
                CoachingRestTemplate.clientHttpRequestFactory(timeout)));
    }

    public static PoolingHttpClientConnectionManager getPoolingConnectionManager() {
        PoolingHttpClientConnectionManager connectionManager =
                new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(HTTP_MAX_CONNECTION_ALLOWED);
        connectionManager.setDefaultMaxPerRoute(HTTP_MAX_CONNECTION_PER_REQUEST);
        return connectionManager;
    }

    private static HttpComponentsClientHttpRequestFactory clientHttpRequestFactory(int timeout) {

        PoolingHttpClientConnectionManager manager =
                CoachingRestTemplate.getPoolingConnectionManager();
        CloseableHttpClient httpClient =
                HttpClientBuilder.create().setConnectionManager(manager).useSystemProperties()
                        .build();
        HttpComponentsClientHttpRequestFactory factory =
                new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(httpClient);
        factory.setReadTimeout(timeout);
        factory.setConnectTimeout(timeout);

        return factory;
    }

    public static CoachingRestTemplate getRequestTemplate(int timeout) {
        CoachingRestTemplate coachingRestTemplate = new CoachingRestTemplate(timeout);
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new RequestResponseLoggingInterceptor());
        coachingRestTemplate.setInterceptors(interceptors);
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(new FormHttpMessageConverter());
        messageConverters.add(new StringHttpMessageConverter());
        messageConverters.add(new ByteArrayHttpMessageConverter());
        messageConverters.add(new ResourceHttpMessageConverter());
        messageConverters.add(new MappingJackson2HttpMessageConverter());
        coachingRestTemplate.setMessageConverters(messageConverters);
        coachingRestTemplate.setMessageConverters(messageConverters);
        return coachingRestTemplate;
    }

    private HttpHeaders createHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        return headers;
    }

    private HttpHeaders createHttpHeaders(MediaType mediaType,
            Map<String, String> additionalHeaders) {
        HttpHeaders headers = new HttpHeaders();
        if (additionalHeaders != null) {
            additionalHeaders.forEach((k, v) -> headers.add(k, v));
        }
        headers.setContentType(mediaType);
        headers.add(PAYTM_REQUEST_ID, MDC.get(PAYTM_REQUEST_ID));
        return headers;
    }

    private HttpHeaders createHttpHeaders(Map<String, String> additionalHeaders) {
        HttpHeaders headers = this.createHttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (additionalHeaders != null) {
            additionalHeaders.forEach((k, v) -> headers.add(k, v));
        }
        return headers;
    }

    public <T> T getForObject(URI url, Map<String, String> headers, Class<T> responseType)
            throws RestClientException {
        HttpEntity<Object> entity = new HttpEntity<Object>(createHttpHeaders(headers));
        ResponseEntity<T> response = this.exchange(url, HttpMethod.GET, entity, responseType);
        return response.getBody();
    }

    public <T> T getForObject(String url, Map<String, String> headers,
            ParameterizedTypeReference<T> parameterizedTypeReference) {
        HttpEntity<String> entity = new HttpEntity<String>(createHttpHeaders(headers));
        ResponseEntity<T> response =
                this.exchange(url, HttpMethod.GET, entity, parameterizedTypeReference);
        return response.getBody();
    }

    @Override
    public <T> T getForObject(URI url, Class<T> responseType) throws RestClientException {
        HttpEntity<String> entity = new HttpEntity<String>(createHttpHeaders());
        ResponseEntity<T> response = this.exchange(url, HttpMethod.GET, entity, responseType);
        return response.getBody();
    }

    public <T> T getForObject(String url, Map<String, String> headers, Class<T> responseType)
            throws RestClientException {
        HttpEntity<String> entity = new HttpEntity<String>(createHttpHeaders(headers));
        ResponseEntity<T> response = this.exchange(url, HttpMethod.GET, entity, responseType);
        return response.getBody();
    }

    public <T> ResponseEntity<T> postRawForObject(String url, Object request, Class<T> responseType,
            Object... uriVariables) throws RestClientException {
        HttpEntity<Object> entity = new HttpEntity<Object>(request, createHttpHeaders());
        ResponseEntity<T> response =
                this.exchange(url, HttpMethod.POST, entity, responseType, uriVariables);
        return response;
    }

    public <T> ResponseEntity<T> postRawForObject(String url, Map<String, String> headers,
            Object request, Class<T> responseType,
            Object... uriVariables) throws RestClientException {
        HttpEntity<Object> entity = new HttpEntity<Object>(request, createHttpHeaders(headers));
        ResponseEntity<T> response =
                this.exchange(url, HttpMethod.POST, entity, responseType, uriVariables);
        return response;
    }

    public <T> T postForObject(String url, Map<String, String> headers, Object request,
            Class<T> responseType, Object... uriVariables) throws RestClientException {
        HttpEntity<Object> entity = new HttpEntity<Object>(request, createHttpHeaders(headers));
        ResponseEntity<T> response =
                this.exchange(url, HttpMethod.POST, entity, responseType, uriVariables);
        return response.getBody();
    }

    public <T> T postForObject(String url, Map<String, String> headers,
            MultiValueMap<String, String> formDataMap, Class<T> responseType,
            Object... uriVariables)
            throws RestClientException {
        HttpEntity<Object> entity = new HttpEntity<Object>(formDataMap, createHttpHeaders(headers));
        ResponseEntity<T> response =
                this.exchange(url, HttpMethod.POST, entity, responseType, uriVariables);
        return response.getBody();
    }

    public <T> T postForObject(String url, MediaType mediaType, Map<String, String> headers,
            MultiValueMap<String, String> formDataMap, Class<T> responseType,
            Object... uriVariables)
            throws RestClientException {
        HttpEntity<Object> entity =
                new HttpEntity<Object>(formDataMap, createHttpHeaders(mediaType, headers));
        ResponseEntity<T> response =
                this.exchange(url, HttpMethod.POST, entity, responseType, uriVariables);
        return response.getBody();
    }

}
