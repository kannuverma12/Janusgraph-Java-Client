package com.paytm.digital.education.application.config.metric;

import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.config.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(1)
public class DatadogFilter extends OncePerRequestFilter {

    private static Logger log = LoggerFactory.getLogger(DatadogFilter.class);

    @Autowired
    private MetricsAgent metricsAgent;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {


        long startTime = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long elapsed = System.currentTimeMillis() - startTime;
            Integer httpCode = response.getStatus();
            String finalName = generateMetricNameFromRequestAndResponse(request);
            log.debug("{} took {} ms with status {}", finalName, elapsed, httpCode.toString());
            metricsAgent.recordExecutionTimeOfApi(finalName, elapsed);
            metricsAgent.recordResponseCodeCount(finalName, httpCode.toString());
        }
    }

    private String generateMetricNameFromRequestAndResponse(HttpServletRequest request) {
        final String requestType = request.getMethod();
        return requestType + "_" + getApiNameFromUri(request.getRequestURI());
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
            return getURIStructure(requestUri);
        }
    }

    private String getURIStructure(String resourcePath) {
        StringBuilder metricsURI = new StringBuilder();
        String numRegex = ".*[0-9].*";
        String[] pattern = resourcePath.split("/");
        for (String part : pattern) {
            if (!part.isEmpty()) {
                metricsURI.append("/");
                if (part.matches(numRegex) && !part.matches("v\\d+")) {
                    metricsURI.append("XXXXX");
                } else {
                    metricsURI.append(part);
                }
            }
        }
        return metricsURI.toString();
    }

}
