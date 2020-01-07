package com.paytm.digital.education.metrics;

import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import lombok.Getter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


@Component
public class DataDogClient {

    private static Logger log = LoggerFactory.getLogger(DataDogClient.class);

    private static final String DELIMITER_DOT = ".";

    @Value("${datadog.prefix}")
    private String              datadogPrefix;

    @Value("${datadog.hostname}")
    private String              datadogHost;

    @Value("${datadog.port}")
    private int                 datadogPort;

    @Value("${datadog.enable}")
    private boolean             isDatadogEnabled;

    @Value("${spring.profiles.active}")
    public String               environment;

    @Autowired
    private DataDogErrorHandler dataDogErrorHandler;

    @Getter
    private StatsDClient        statsDClient;

    @PostConstruct
    public void initDDClient() {
        log.info("Initializing Data Dog Client with params prefix : {}, host : {}, port : {}",
                datadogPrefix, datadogHost, datadogPort);
        statsDClient = new NonBlockingStatsDClient(datadogPrefix, datadogHost,
                datadogPort, null, dataDogErrorHandler);
        log.info("Data Dog Client Initialized...");
    }

    @PreDestroy
    public void destroyDDClient() {
        if (statsDClient != null) {
            statsDClient.close();
        }
    }

    public void recordExecutionTime(String metricName, long executionTime) {
        if (!isDatadogEnabled) {
            log.warn("Data Dog Client is disabled");
        } else if (statsDClient != null) {
            String aspect = new StringBuilder(environment)
                    .append(DELIMITER_DOT).append(metricName)
                    .append(DELIMITER_DOT).append("exeTime")
                    .toString();
            statsDClient.recordExecutionTime(aspect, executionTime);
        } else {
            log.error("Data Dog Client is not initialized properly, statsDClient : {}",
                    statsDClient);
        }
    }

    public void recordResponseCodeCount(String metricName, HttpStatus httpStatusCode) {
        if (!isDatadogEnabled) {
            log.warn("Data Dog Client is disabled");
        } else if (statsDClient != null) {
            statsDClient.increment(environment + DELIMITER_DOT + "responseCodeCount",
                    "metricName:" + metricName, "httpStatusCode:" + httpStatusCode.name());
        } else {
            log.error("Data Dog Client is not initialized properly, statsDClient : {}",
                    statsDClient);
        }
    }

    public void recordRequestRate(String metricName, long requestCount) {
        if (!isDatadogEnabled) {
            log.warn("Data Dog Client is disabled");
        } else if (statsDClient != null) {
            String aspect = new StringBuilder(environment)
                    .append(DELIMITER_DOT).append(metricName)
                    .append(DELIMITER_DOT).append("requestRate")
                    .toString();
            statsDClient.recordGaugeValue(aspect, requestCount);
        } else {
            log.error("Data Dog Client is not initialized properly, statsDClient : {}",
                    statsDClient);
        }
    }

    public void increment(String metricName, String... tags) {
        if (!isDatadogEnabled) {
            log.warn("Data Dog Client is disabled");
        } else if (statsDClient != null) {
            String aspect = new StringBuilder(environment)
                    .append(DELIMITER_DOT).append(metricName)
                    .toString();
            statsDClient.increment(aspect, tags);
        } else {
            log.error("Data Dog Client is not initialized properly, statsDClient : {}",
                    statsDClient);
        }
    }
}
