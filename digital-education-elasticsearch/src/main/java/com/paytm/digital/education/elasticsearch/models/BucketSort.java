package com.paytm.digital.education.elasticsearch.models;

import com.paytm.digital.education.elasticsearch.enums.BucketAggregationSortParms;
import com.paytm.digital.education.elasticsearch.enums.DataSortOrder;
import lombok.Data;

@Data
public class BucketSort {

    private BucketAggregationSortParms key;

    private DataSortOrder order;
}
