package com.paytm.digital.education.elasticsearch.models;

import lombok.Data;

@Data
public class MetricAggregationResponse implements AggregationResponse {

    private String key;

    private double minValue;

    private double maxValue;

}
