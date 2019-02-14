package com.paytm.digital.education.elasticsearch.models;

import com.paytm.digital.education.elasticsearch.enums.AggregationType;
import lombok.Data;

@Data
public class AggregateField {

    private String          name;

    private String          path;

    private AggregationType type;

    private BucketSort      bucketsOrder;
}
