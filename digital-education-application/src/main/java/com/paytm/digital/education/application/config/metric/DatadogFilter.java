package com.paytm.digital.education.application.config.metric;

import java.io.IOException;
import java.util.Objects;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.core.config.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(1)
@Slf4j
public class DatadogFilter extends OncePerRequestFilter {

  @Autowired
  private MetricsAgent metricsAgent;

  private String generateMetricNameFromRequestAndResponse(HttpServletRequest request, HttpServletResponse response) {
    final String requestURI = request.getRequestURI();
    final String requestType = request.getMethod();
    final String apiNameHeaderValue = response.getHeader("method-name");
    final String apiName = Objects.nonNull(apiNameHeaderValue) ? apiNameHeaderValue : requestURI;
    return requestType + apiName;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    long startTime = System.currentTimeMillis();
    filterChain.doFilter(request, response);
    long elapsed = System.currentTimeMillis() - startTime;

    Integer httpCode = response.getStatus();
    String errorCode = response.getHeader("Error-Code");

    String metricName = generateMetricNameFromRequestAndResponse(request, response);
    log.debug("Generated metric name - {}", metricName);
    log.debug(metricName + " took " + elapsed + " ms" + " with status " + httpCode
        + " and error code " + errorCode);
    metricsAgent.recordExecutionTimeOfApi(metricName, elapsed);
    metricsAgent.incrementApiCount(metricName);
    if (Objects.isNull(errorCode)) {
      metricsAgent.recordResponseCodeCount(metricName, httpCode.toString());
    } else {
      metricsAgent.recordResponseCodeCount(metricName, httpCode.toString(), errorCode);
    }
  }

  @Override
  public void destroy() {

  }

}
