package com.paytm.digital.education.explore.request.dto.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.enums.es.DataSortOrder;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class Classification {

    @JsonProperty("is_classified")
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

