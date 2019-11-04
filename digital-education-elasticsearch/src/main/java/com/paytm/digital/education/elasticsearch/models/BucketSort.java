package com.paytm.digital.education.elasticsearch.models;

import com.paytm.digital.education.enums.es.BucketAggregationSortParms;
import com.paytm.digital.education.enums.es.DataSortOrder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BucketSort {

    private BucketAggregationSortParms key;

    private DataSortOrder order;
}
