package com.paytm.digital.education.application.config.metric;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class MetricsAgent {

    public enum Metric {
        SUCCESS, FAILURE, PENDING, HIT
    }

    private StatsDClient metricClient;

    @Value("${datadog.prefix}")
    private String datadogPrefix;

    @Value("${datadog.hostname}")
    private String datadogHostname;

    @Value("${datadog.port}")
    private Integer datadogPort;

    @Value("${spring.profiles.active}")
    private String digiEduEnv;

    @PostConstruct
    public void init() {
        metricClient = new NonBlockingStatsDClient(datadogPrefix, datadogHostname, datadogPort,
                "application:" + datadogPrefix);

    }

    /**
     * Used to record execution time of api
     */
    public void recordExecutionTimeOfApi(String apiName, long timeTaken) {
        metricClient.recordExecutionTime("DIGI_EDU_LATENCY", timeTaken, "api_name:" + apiName,
                "environment:" + digiEduEnv);
    }

    /**
     * Used to increase counter of api
     */
    public void incrementApiCount(String apiName) {
        metricClient.increment("DIGI_EDU_HTTP_COUNT", "api_name:" + apiName, "environment:" + digiEduEnv);
    }

    /**
     * Used to record HTTP code of api
     */
    public void recordResponseCodeCount(String apiName, String httpCode) {
        metricClient.increment("DIGI_EDU_HTTP_CODE_COUNT", "api_count:" + apiName, "http_code:" + httpCode,
                "environment:" + digiEduEnv);
    }

    /**
     * Used to record ERROR code of api
     */
    public void recordResponseCodeCount(String apiName, String httpCode, String responseCode) {
        metricClient.increment("response_code_count", "api_count:" + apiName, "http_code:" + httpCode,
                "response_code:" + responseCode, "environment:" + digiEduEnv);
    }

    /**
     * Used to record execution time of function, mostly used via Aspect
     *
     */
    public void recordExecutionTimeOfFn(String fnName, long timeTaken, String callerMethod) {
        metricClient.recordExecutionTime("fn_execution_time", timeTaken,
                "fn_name:" + fnName, "caller_method:" + callerMethod, "environment:" + digiEduEnv);
    }

    /**
     * Used to increase counter of function invocations, mostly used via Aspect
     *
     */
    public void incrementFnCount(String fnName, String callerMethod) {
        metricClient.increment("fn_invoke_counter",
                "fn_name:" + fnName, "caller_method:" + callerMethod, "environment:" + digiEduEnv);
    }

    /**
     * Used to increase counter of function error events, mostly used via Aspect
     *
     */
    public void incrementfnErrorCount(String fnName, String exceptionName, String callerMethod) {
        metricClient.increment("fn_error_counter",
                "fn_name:" + fnName, "caller_method:" + callerMethod,
                "error_name:" + exceptionName, "environment:" + digiEduEnv);
    }
}
