package com.paytm.digital.education.coaching.consumer.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.coaching.constants.CoachingConstants;
import com.paytm.digital.education.elasticsearch.enums.DataSortOrder;
import com.paytm.digital.education.enums.Client;
import com.paytm.digital.education.enums.EducationEntity;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class SearchRequest {

    @JsonProperty("term")
    private String term;

    @JsonProperty("filter")
    private Map<String, List<Object>> filter;

    @JsonProperty("entity")
    @NotNull
    private EducationEntity entity;

    @JsonProperty("offset")
    @Min(0)
    @Max(CoachingConstants.Search.SEARCH_REQUEST_MAX_OFFSET)
    @NotNull
    private Integer offset = CoachingConstants.Search.DEFAULT_OFFSET;

    @JsonProperty("limit")
    @Min(0)
    @Max(CoachingConstants.Search.SEARCH_REQUEST_MAX_LIMIT)
    @NotNull
    private Integer limit = CoachingConstants.Search.DEFAULT_SIZE;

    @JsonProperty("clear_filter")
    private boolean clearFilters = false;

    @JsonProperty("fetch_filter")
    @NotNull
    private Boolean fetchFilter = true;
    
    @JsonProperty("sort_order")
    private LinkedHashMap<String, DataSortOrder> sortOrder;

    @JsonIgnore
    private Boolean fetchSearchResults = true;

    @JsonIgnore
    private boolean fetchSearchResultsPerFilter;

    @JsonIgnore
    private String dataPerFilter;

    @JsonIgnore
    private Client client;

}
