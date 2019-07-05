package com.paytm.digital.education.form.service.impl;

import com.paytm.digital.education.form.config.RestConfiguration;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@AllArgsConstructor
public class RestApiService {

    private RestConfiguration rest;

    public <T> T get(String url, Map<String, ?> queryParams, Type responseClassType) {
        ResponseEntity<T> responseEntity;
        HttpEntity<Object> requestEntity = new HttpEntity<>(HttpHeaders.EMPTY);
        URI uri = getURI(url, queryParams);
        try {
            responseEntity =
                    rest.getRestTemplate().exchange(uri, HttpMethod.GET, requestEntity,
                            new ParameterizedTypeReference<T>() {
                                @Override
                                public Type getType() {
                                    return responseClassType;
                                }
                            });
        } catch (Exception e) {
            throw e;
        }
        return getResponseBody(responseEntity);
    }

    public <T> T post(
            final String url, Class<T> clazz, String requestBody,
            final Map<String, String> headers) {
        if (Objects.isNull(headers) || headers.isEmpty()) {
            throw new IllegalArgumentException("Headers Can't be null or empty");
        }
        if (Objects.isNull(url) || StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("Url can't be null or blank");
        }
        if (Objects.isNull(clazz)) {
            throw new IllegalArgumentException("Class type cant be null");
        }

        if (Objects.isNull(requestBody)) {
            throw new IllegalArgumentException("RequestBody cant be null");
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            httpHeaders.add(entry.getKey(), entry.getValue());
        }
        HttpEntity<Object> httpEntity = new HttpEntity<Object>(requestBody, httpHeaders);
        ResponseEntity<T> responseEntity =
                rest.getRestTemplate().exchange(url, HttpMethod.POST, httpEntity, clazz);
        return responseEntity.getBody();
    }

    private URI getURI(String url, Map<String, ?> queryParams) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        if (!CollectionUtils.isEmpty(queryParams)) {
            for (Map.Entry<String, ?> entry : queryParams.entrySet()) {
                builder = builder.queryParam(entry.getKey(), entry.getValue());
            }
        }
        return builder.build().encode().toUri();
    }

    private <T> T getResponseBody(ResponseEntity<T> responseEntity) {
        if (responseEntity != null) {
            return responseEntity.getBody();
        }
        return null;
    }



}
