package com.paytm.digital.education.metrics;

import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import com.timgroup.statsd.StatsDClientErrorHandler;

import org.springframework.stereotype.Component;

@Component
public class DataDogErrorHandler implements StatsDClientErrorHandler {

    private static Logger log = LoggerFactory.getLogger(DataDogErrorHandler.class);

    @Override
    public void handle(Exception ex) {
        log.error("Exception in Data Dog StatsDClient ", ex);
    }

}
