package com.paytm.digital.education.coaching.connections.rest;

import com.paytm.digital.education.utility.JsonUtils;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class RequestResponseLoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger log =
            LoggerFactory.getLogger(RequestResponseLoggingInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
            ClientHttpRequestExecution execution) throws IOException {
        Map<String, Object> payload = new HashMap<>();
        try {
            payload.put("request", logRequest(request, body));
            ClientHttpResponse response = execution.execute(request, body);
            payload.put("response", logResponse(response));
            return response;
        } finally {
            log.info(JsonUtils.toJson(payload));
        }
    }

    private Map<String, Object> logRequest(HttpRequest request, byte[] body) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("URI", request.getURI());
        data.put("method", request.getMethod() != null ? request.getMethod().toString() : "");
        data.put("query", request.getURI().getQuery());
        data.put("headers", request.getHeaders());
        data.put("body", new String(body, "UTF-8"));
        return data;
    }


    private Map<String, Object> logResponse(ClientHttpResponse response) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("code", response.getStatusCode().toString());
        data.put("status", response.getStatusText());
        data.put("headers", response.getHeaders());
        data.put("body", StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
        return data;
    }
}
