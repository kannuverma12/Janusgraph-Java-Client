package com.paytm.digital.education.explore.request.dto.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.enums.EducationEntity;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchRequest {

    @JsonProperty("term")
    private String term;

    @JsonProperty("filter")
    private Map<String, List<Object>> filter;

    @JsonProperty("entity")
    private EducationEntity entity;

    @JsonProperty("offset")
    private int offset;

    @JsonProperty("limit")
    private int limit;

    @JsonProperty("fields")
    private List<String> fields;

    @JsonProperty("field_group")
    private String fieldGroup;

    @JsonProperty("fetch_filter")
    private boolean fetchFilter = true;
   
}
