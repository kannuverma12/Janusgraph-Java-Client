package com.paytm.digital.education.application.config.metric;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import javax.annotation.PostConstruct;

@Component
public class MetricsAgent {

  public enum Metric {
    SUCCESS, FAILURE, PENDING, HIT
  }

  private StatsDClient metricClient;

  @Value("${datadog.prefix}")
  private String DATADOG_PREFIX;

  @Value("${datadog.hostname}")
  private String DATADOG_HOSTNAME;

  @Value("${datadog.port}")
  private Integer DATADOG_PORT;

  @Value("${digital.education.env}")
  private String DIGI_EDU_ENV;

  @PostConstruct
  public void init() {
    metricClient = new NonBlockingStatsDClient(DATADOG_PREFIX, DATADOG_HOSTNAME, DATADOG_PORT,
        "application:" + DATADOG_PREFIX);

  }

  /**
   * Used to record execution time of api
   */
  public void recordExecutionTimeOfApi(String apiName, long timeTaken) {
    metricClient.recordExecutionTime("DIGI_EDU_LATENCY", timeTaken, "api_name:" + apiName,
        "environment:" + DIGI_EDU_ENV);
  }

  /**
   * Used to increase counter of api
   */
  public void incrementApiCount(String apiName) {
    //metricClient.increment("DIGI_EDU_HTTP_COUNT." + apiName, "api_name:" + apiName, "environment:" + DIGI_EDU_ENV);
    metricClient.increment("DIGI_EDU_HTTP_COUNT", "api_name:" + apiName, "environment:" + DIGI_EDU_ENV);
  }

  /**
   * Used to record HTTP code of api
   */
  public void recordResponseCodeCount(String apiName, String httpCode) {
    metricClient.increment("DIGI_EDU_HTTP_CODE_COUNT", "api_count:" + apiName, "http_code:" + httpCode,
        "environment:" + DIGI_EDU_ENV);
  }

  /**
   * Used to record ERROR code of api
   */
  public void recordResponseCodeCount(String apiName, String httpCode, String responseCode) {
    metricClient.increment("response_code_count", "api_count:" + apiName, "http_code:" + httpCode,
        "response_code:" + responseCode, "environment:" + DIGI_EDU_ENV);
  }

  /**
   * Used to record execution time of function, mostly used via Aspect
   *
   * @param string
   */
  public void recordExecutionTimeOfFn(String fnName, long timeTaken, String fnType) {
    metricClient.recordExecutionTime("fn_execution_time", timeTaken, "fn_name:" + fnName, "fn_type:" + fnType,
        "environment:" + DIGI_EDU_ENV);
  }

  /**
   * Used to increase counter of function, mostly used via Aspect
   *
   * @param string
   */
  public void incrementFnCount(String fnName, String fnType, String metricName) {
    metricClient.increment(metricName, "fn_name:" + fnName, "fn_type:" + fnType,
        "environment:" + DIGI_EDU_ENV);
  }
}
