package com.paytm.digital.education.application.config.metric;

import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.config.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UrlPathHelper;

import java.io.IOException;
import java.net.URI;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Order(1)
public class DatadogFilter extends OncePerRequestFilter {

    private static Logger log = LoggerFactory.getLogger(DatadogFilter.class);

    @Autowired
    private MetricsAgent metricsAgent;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {


        long startTime = System.currentTimeMillis();
        try{
            filterChain.doFilter(request, response);
        } finally {
            long elapsed = System.currentTimeMillis() - startTime;

            String requestType = request.getMethod();
            Integer httpCode = response.getStatus();
            String resourcePath = (new UrlPathHelper()).getPathWithinApplication(request);
            resourcePath = getURIStructure(resourcePath);
            String finalName = requestType + "_" + resourcePath;
            log.debug("{} took {} ms with status {}", finalName, elapsed, httpCode.toString());
            metricsAgent.recordExecutionTimeOfApi(finalName, elapsed);
            metricsAgent.recordResponseCodeCount(finalName, httpCode.toString());
        }
    }

    private String getURIStructure(String resourcePath){
        StringBuilder metricsURI = new StringBuilder();
        String numRegex   = ".*[0-9].*";
        String[] pattern = resourcePath.split("/");
        for (String part : pattern){
            if (!part.isEmpty()){
                metricsURI.append("/");
                if (part.matches(numRegex) && !part.matches("v\\d+")) {
                    metricsURI.append("XXXXX");
                }else{
                    metricsURI.append(part);
                }
            }
        }
        return metricsURI.toString();
    }

}
