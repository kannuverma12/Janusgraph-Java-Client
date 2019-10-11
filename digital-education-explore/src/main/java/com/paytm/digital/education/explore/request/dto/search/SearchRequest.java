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
import com.paytm.digital.education.explore.es.model.GeoLocation;
import com.paytm.digital.education.explore.sro.request.FieldsAndFieldGroupRequest;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchRequest {

    @JsonProperty("term")
    private String term;

    @JsonProperty("filter")
    private Map<String, List<Object>> filter;

    @JsonProperty("entity")
    @NotNull
    private EducationEntity entity;

    @JsonProperty("offset")
    @Min(value = 0, message = "Offset should be greater than or equal to 0")
    @Max(value = SEARCH_REQUEST_MAX_OFFSET, message = "Offset should be less than or equal to  "
            + SEARCH_REQUEST_MAX_OFFSET)
    @NotNull
    private Integer offset = DEFAULT_OFFSET;

    @JsonProperty("limit")
    @Min(value = 0, message = "Limit should be greater than or equal to 0")
    @Max(value = SEARCH_REQUEST_MAX_LIMIT, message = "Limit should be less than or equal to "
            + SEARCH_REQUEST_MAX_LIMIT)
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

    @JsonProperty("location")
    private GeoLocation geoLocation;

    @JsonIgnore
    private List<String> dataPerFilter;

    @JsonIgnore
    private boolean fetchSearchResultsPerFilter;

    @JsonProperty("radius")
    private Integer radius;
}
