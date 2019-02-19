package com.paytm.digital.education.elasticsearch.models;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ElasticResponse<T> {

    private long                             searchQueryExecutionTime;

    private long                             aggregationQueryExecutionTime;

    private List<T>                          documents;

    private Map<String, AggregationResponse> aggregationResponse;

}
