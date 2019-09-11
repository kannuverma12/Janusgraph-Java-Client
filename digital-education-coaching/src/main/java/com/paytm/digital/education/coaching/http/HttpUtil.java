package com.paytm.digital.education.coaching.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paytm.digital.education.coaching.exeptions.CoachingBaseException;
import com.paytm.digital.education.coaching.exeptions.CoachingExceptionTranslator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class HttpUtil {

    @Autowired
    private ObjectMapper objectMapper;

    public <T> ResponseEntity<T> exchange(final RestTemplate restTemplate,
            final HttpRequestDetails httpRequestDetails, final Class<T> responseClass)
            throws CoachingBaseException {
        final String requestAPIName = httpRequestDetails.getRequestApiName();
        String requestBody = buildRequestBody(httpRequestDetails, requestAPIName);
        String url = buildUrl(httpRequestDetails);
        final HttpEntity<String> httpEntity = new HttpEntity<>(requestBody,
                httpRequestDetails.getHeaders());

        ResponseEntity<T> response = null;
        log.info("Going to execute call for url: {}, httpEntity: {}", url, httpEntity);

        final long requestStartTime = System.currentTimeMillis();
        try {
            response = restTemplate.exchange(url, httpRequestDetails.getRequestMethod(),
                    httpEntity, responseClass);
            log.info("Response of {} call: {}", requestAPIName, response.getBody());
        } catch (final Exception e) {
            log.error("Exception occurred for {} for Url: {}, httpEntity: {}, exception: ",
                    requestAPIName, url, httpEntity, e);
            CoachingExceptionTranslator.translate(e, requestAPIName);
        } finally {
            logRequestLatency(httpRequestDetails, requestAPIName, httpEntity, response,
                    requestStartTime);
        }
        return response;
    }

    private String buildRequestBody(HttpRequestDetails httpRequestDetails, String requestAPIName)
            throws CoachingBaseException {
        String requestBody = null;
        try {
            requestBody = this.objectMapper.writeValueAsString(httpRequestDetails.getBody());
        } catch (JsonProcessingException jpe) {
            log.error("Error occurred while parsing requestBody: {}, exception: {}",
                    requestBody, jpe.getMessage());
            CoachingExceptionTranslator.translate(jpe, requestAPIName);
        }
        return requestBody;
    }

    private String buildUrl(HttpRequestDetails httpRequestDetails) {
        final UriComponentsBuilder builder =
                UriComponentsBuilder.fromUriString(httpRequestDetails.getUrl());

        String url;
        if (!CollectionUtils.isEmpty(httpRequestDetails.getMultiQueryParams())) {
            url = addQueryParams(builder, httpRequestDetails.getQueryParams(),
                    httpRequestDetails.getMultiQueryParams());
        } else {
            url = addQueryParams(builder, httpRequestDetails.getQueryParams());
        }
        return url;
    }

    private String addQueryParams(final UriComponentsBuilder builder,
            final Map<String, String> queryMap) {
        if (!CollectionUtils.isEmpty(queryMap)) {
            for (String key : queryMap.keySet()) {
                builder.queryParam(key, queryMap.get(key));
            }
        }
        return builder.buildAndExpand().toUriString();
    }

    private String addQueryParams(final UriComponentsBuilder builder,
            final Map<String, String> queryMap,
            final Map<String, List<String>> multiQueryMap) {
        if (!CollectionUtils.isEmpty(queryMap)) {
            for (String key : queryMap.keySet()) {
                builder.queryParam(key, queryMap.get(key));
            }
        }
        if (!CollectionUtils.isEmpty(multiQueryMap)) {
            for (String key : multiQueryMap.keySet()) {
                List<String> queryList = multiQueryMap.get(key);
                for (String ele : queryList) {
                    builder.queryParam(key, ele);
                }
            }
        }
        return builder.buildAndExpand().toUriString();
    }

    private <T> void logRequestLatency(HttpRequestDetails httpRequestDetails, String requestAPIName,
            HttpEntity<String> httpEntity, ResponseEntity<T> response, long requestStartTime) {
        final long requestEndTime = System.currentTimeMillis();
        final long requestLatency = requestEndTime - requestStartTime;
        if (requestLatency > httpRequestDetails.getExpectedLatency()) {
            log.warn(
                    "Took more than expected time in execution for: {}, timeTaken: {}, "
                            + "expectedTime: {}, request: {}, response: {}",
                    requestAPIName, requestLatency, httpRequestDetails.getExpectedLatency(),
                    httpEntity, response);
        }
    }
}
