package com.paytm.digital.education.form.producer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
@AllArgsConstructor
public class TopicPublishCallback implements ListenableFutureCallback<SendResult<String, String>> {

    private String topic;
    private String key;
    private String message;

    @Override
    public void onSuccess(SendResult<String, String> result) {
    }

    @Override
    public void onFailure(Throwable e) {
        if (key == null) {
            log.error("KAFKA_PUBLISHER_FAILED MESSAGE TOPIC:{} MESSAGE:{}", topic, message, e);
        } else {
            log.error("KAFKA_PUBLISHER_FAILED MESSAGE TOPIC:{} KEY:{} MESSAGE:{}", topic, key, message, e);
        }
        // todo: send metric
    }

}
