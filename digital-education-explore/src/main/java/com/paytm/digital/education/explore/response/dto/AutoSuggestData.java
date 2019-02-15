package com.paytm.digital.education.explore.response.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AutoSuggestData {

    @JsonProperty("entity_type")
    private String entityType;

    @JsonProperty("results")
    private List<SuggestResult> results;
}
