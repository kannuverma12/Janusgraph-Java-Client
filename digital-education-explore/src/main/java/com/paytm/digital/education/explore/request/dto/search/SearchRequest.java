package com.paytm.digital.education.explore.request.dto.search;

import static com.paytm.digital.education.explore.constants.ExploreConstants.DEFAULT_OFFSET;
import static com.paytm.digital.education.explore.constants.ExploreConstants.DEFAULT_SIZE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SEARCH_REQUEST_MAX_LIMIT;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SEARCH_REQUEST_MAX_OFFSET;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.elasticsearch.enums.DataSortOrder;
import com.paytm.digital.education.explore.enums.Client;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.sro.request.FieldsAndFieldGroupRequest;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchRequest extends FieldsAndFieldGroupRequest {

    @JsonProperty("term")
    private String term;

    @JsonProperty("filter")
    private Map<String, List<Object>> filter;

    @JsonProperty("entity")
    @NotNull
    private EducationEntity entity;

    @JsonProperty("offset")
    @Min(0)
    @Max(SEARCH_REQUEST_MAX_OFFSET)
    @NotNull
    private Integer offset = DEFAULT_OFFSET;

    @JsonProperty("limit")
    @Min(0)
    @Max(SEARCH_REQUEST_MAX_LIMIT)
    @NotNull
    private Integer limit = DEFAULT_SIZE;

    @JsonProperty("clear_filter")
    private boolean clearFilters = false;

    @JsonProperty("fetch_filter")
    @NotNull
    private Boolean fetchFilter = true;

    @JsonIgnore
    private Boolean fetchSearchResults = true;

    @JsonIgnore
    private Client client;

    @JsonProperty("meta")
    private Classification classificationData;

    @JsonProperty("sort_order")
    private LinkedHashMap<String, DataSortOrder> sortOrder;
}
