package com.paytm.digital.education.application.config.metric;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.config.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Order(1)
@Slf4j
public class DatadogFilter extends OncePerRequestFilter {

    @Autowired
    private MetricsAgent metricsAgent;

    private String generateMetricNameFromRequestAndResponse(HttpServletRequest request, HttpServletResponse response) {
        final String requestType = request.getMethod();
        return requestType + "_" + getApiNameFromUri(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        filterChain.doFilter(request, response);
        long elapsed = System.currentTimeMillis() - startTime;
        Integer httpCode = response.getStatus();

        String metricName = generateMetricNameFromRequestAndResponse(request, response);
        log.debug(metricName + " took " + elapsed + " ms" + " with status " + httpCode);
        metricsAgent.recordExecutionTimeOfApi(metricName, elapsed);
        metricsAgent.incrementApiCount(metricName);
        metricsAgent.recordResponseCodeCount(metricName, httpCode.toString());
        //TODO - Combine all above metric into one in later phase
    }

    @Override
    public void destroy() {

    }

    private String getApiNameFromUri(String requestUri) {
        requestUri = requestUri.toLowerCase();
        if (StringUtils.isEmpty(requestUri)) {
            return "";
        } else if (requestUri.contains("/v1/page")) {
            return "/v1/page";
        } else if (requestUri.contains("/v1/course")) {
            return "/v1/course";
        } else if (requestUri.contains("/auth/v1/exam")) {
            return "/auth/v1/exam";
        } else if (requestUri.contains("/auth/v1/institute")) {
            return "/auth/v1/institute";
        } else {
            return requestUri;
        }
    }

}
