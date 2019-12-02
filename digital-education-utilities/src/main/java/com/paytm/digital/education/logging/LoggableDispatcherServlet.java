package com.paytm.digital.education.logging;

import com.paytm.digital.education.utility.JsonUtils;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static com.paytm.digital.education.constant.LoggingConstants.SEPERATOR;
import static com.paytm.digital.education.constant.LoggingConstants.SUCCESS_STATUS_SERIES;
import static com.paytm.digital.education.constant.LoggingConstants.SUCCESS_REQUEST_LOGGING_FREQUENCY;

public class LoggableDispatcherServlet extends DispatcherServlet {

    private static final Logger               log                         =
            LoggerFactory.getLogger(LoggableDispatcherServlet.class);
    private static       Map<String, Integer> requestUriVsSuccessCountMap = new HashMap<>();

    @Override
    protected void doDispatch(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        if (!(request instanceof ContentCachingRequestWrapper)) {
            request = new ContentCachingRequestWrapper(request);
        }
        if (!(response instanceof ContentCachingResponseWrapper)) {
            response = new ContentCachingResponseWrapper(response);
        }
        HandlerExecutionChain handler = getHandler(request);
        try {
            super.doDispatch(request, response);
        } finally {
            log(request, response, handler);
            updateResponse(response);
        }
    }

    private void log(HttpServletRequest requestToCache, HttpServletResponse responseToCache,
            HandlerExecutionChain handler) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("ReqType", "Incoming");
        map.put("HttpMethod", requestToCache.getMethod());
        map.put("Path", requestToCache.getRequestURI());

        String requestUriKey =
                requestToCache.getMethod() + SEPERATOR + requestToCache.getRequestURI();

        if (isResponse2xxSuccessful(responseToCache.getStatus())) {
            int requestCount = requestUriVsSuccessCountMap.containsKey(requestUriKey)
                     && Objects.nonNull(requestUriVsSuccessCountMap.get(requestUriKey))
                    ? requestUriVsSuccessCountMap.get(requestUriKey) + 1 : 1;

            if (requestCount < SUCCESS_REQUEST_LOGGING_FREQUENCY) {
                requestUriVsSuccessCountMap.put(requestUriKey, requestCount);
                return;
            } else {
                requestUriVsSuccessCountMap.put(requestUriKey, 0);
            }
        }

        Map<String, Object> reqHeaderMap = new HashMap<>();
        Enumeration<String> reqHeaderNames = requestToCache.getHeaderNames();
        while (reqHeaderNames.hasMoreElements()) {
            String headerName = reqHeaderNames.nextElement();
            Enumeration<String> headers = requestToCache.getHeaders(headerName);
            while (headers.hasMoreElements()) {
                String headerValue = headers.nextElement();
                reqHeaderMap.put(headerName, headerValue);
            }
        }
        map.put("ReqHeaders", reqHeaderMap);
        map.put("ReqBody", getRequestPayload(requestToCache));
        map.put("ClientIp", requestToCache.getRemoteAddr());
        map.put("HttpStatus", responseToCache.getStatus());
        Map<String, Object> resHeaderMap = new HashMap<>();
        Collection<String> resHeaderNames = responseToCache.getHeaderNames();
        for (String headerName : resHeaderNames) {
            Collection<String> headerValues = responseToCache.getHeaders(headerName);
            for (String headerValue : headerValues) {
                resHeaderMap.put(headerName, headerValue);
            }
        }
        map.put("ResHeaders", resHeaderMap);
        map.put("ResBody", getResponsePayload(responseToCache));
        log.info(JsonUtils.toJson(map));
    }

    private boolean isResponse2xxSuccessful(int status) {
        int httpCodeSeries = status / 100;
        return SUCCESS_STATUS_SERIES == httpCodeSeries;
    }

    private String getRequestPayload(HttpServletRequest request) {
        ContentCachingRequestWrapper wrapper =
                WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                try {
                    return new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
                } catch (UnsupportedEncodingException ex) {
                    log.error("getRequestPayload | Error string generation | Error:",
                            ex);
                }
            }
        }
        return "";
    }

    private String getResponsePayload(HttpServletResponse response) {
        ContentCachingResponseWrapper wrapper =
                WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                try {
                    return new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
                } catch (UnsupportedEncodingException ex) {
                    log.error("getResponsePayload | Error string generation | Error:",
                            ex);
                }
            }
        }
        return "";
    }

    private void updateResponse(HttpServletResponse response) throws IOException {
        ContentCachingResponseWrapper responseWrapper =
                WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        if (responseWrapper != null) {
            responseWrapper.copyBodyToResponse();
        }
    }
}
