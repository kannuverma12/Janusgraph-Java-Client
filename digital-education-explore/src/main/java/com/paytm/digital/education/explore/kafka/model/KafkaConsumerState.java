package com.paytm.digital.education.explore.kafka.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KafkaConsumerState {
    private String topic;
    private int    partition;
    private long   offset;
}
