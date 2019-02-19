package com.paytm.digital.education.elasticsearch.models;

import lombok.Data;

@Data
public class ElasticRequest {

    private String           queryTerm;

    private String           index;

    private String           analyzer;

    private SearchField[]    searchFields;

    private FilterField[]    filterFields;

    private SortField[]      sortFields;

    private AggregateField[] aggregateFields;

    private Integer          offSet;

    private Integer          limit;

    private boolean          isAggregationRequest;

    private boolean          isSearchRequest;

}
