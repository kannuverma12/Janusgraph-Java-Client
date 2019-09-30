package com.paytm.digital.education.elasticsearch.models;

import com.paytm.digital.education.enums.es.AggregationType;
import lombok.Data;

@Data
public class AggregateField {

    private String          name;

    private String          path;

    private AggregationType type;

    private BucketSort      bucketsOrder;

    private SortField[]     sortFields;

    private String[]        values;

    private String childTermsFieldName;

}
