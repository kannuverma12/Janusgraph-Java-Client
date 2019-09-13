package com.paytm.digital.education.explore.service.external;

import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.explore.config.RestConfig;
import com.paytm.digital.education.mapping.ErrorEnum;
import com.paytm.digital.education.utility.JsonUtils;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@AllArgsConstructor
public class BaseRestApiService {

    private RestConfig rest;

    public <T> T get(String url, Map<String, ?> queryParams, HttpHeaders httpHeaders,
            Type responseClassType, List<String> pathvariablesInorder) {
        ResponseEntity<T> responseEntity;
        HttpEntity<Object> requestEntity = new HttpEntity<>(httpHeaders);
        URI uri = getURI(url, queryParams, pathvariablesInorder);
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
        log.info("Http request : {}", httpEntity);
        if (responseEntity.getStatusCodeValue() != 200) {
            throw new BadRequestException(ErrorEnum.HTTP_REQUEST_FAILED,
                    ErrorEnum.HTTP_REQUEST_FAILED.getExternalMessage());
        }
        return responseEntity.getBody();
    }

    public URI getURI(String url, Map<String, ?> queryParams, List<String> pathVariablesInOrder) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        if (!CollectionUtils.isEmpty(pathVariablesInOrder)) {
            for (String var : pathVariablesInOrder) {
                builder.path(var);
            }
        }
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
