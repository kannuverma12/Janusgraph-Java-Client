package com.paytm.digital.education.explore.response.dto.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.elasticsearch.enums.DataSortOrder;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClassificationResponse {

    @JsonProperty("classified")
    private boolean classified;

    @JsonProperty("location_classified")
    private boolean isLocationClassified;

    @JsonProperty("sort_params")
    private LinkedHashMap<String, DataSortOrder> sortParams;

    @JsonProperty("filters")
    private Map<String, List<Object>> filters;

    @JsonProperty("mapped_keyword")
    private String term;

}
