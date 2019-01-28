package com.paytm.digital.education.application.config.metric;

import com.timgroup.statsd.StatsDClientErrorHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DataDogErrorHandler implements StatsDClientErrorHandler {

    @Override
    public void handle(Exception ex) {
        log.error("Exception in Data Dog StatsDClient ", ex);
    }

}
