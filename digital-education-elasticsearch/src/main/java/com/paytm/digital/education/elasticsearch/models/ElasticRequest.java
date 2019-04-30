package com.paytm.digital.education.elasticsearch.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Data
@ToString
public class ElasticRequest {

    private String queryTerm;

    private String index;

    private String analyzer;

    private SearchField[] searchFields;
    
    private FilterField[] filterFields;

    private SortField[] sortFields;

    private AggregateField[] aggregateFields;

    private Integer offSet;

    private Integer limit;

    private boolean isAggregationRequest;

    private boolean isSearchRequest;

    @JsonIgnore
    public Map<String, FilterField> getFilterFieldDataMap() {
        Map<String, FilterField> filterFieldMap = new HashMap<>();
        if (this.filterFields != null) {
            for (FilterField filterField : filterFields) {
                filterFieldMap.put(filterField.getName(), filterField);
            }
        }
        return filterFieldMap;
    }
}
