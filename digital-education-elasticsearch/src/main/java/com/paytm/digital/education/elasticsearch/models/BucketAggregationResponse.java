package com.paytm.digital.education.elasticsearch.models;

import lombok.Data;
import java.util.List;

@Data
public class BucketAggregationResponse implements AggregationResponse {

    private List<Bucket>    buckets;

}
