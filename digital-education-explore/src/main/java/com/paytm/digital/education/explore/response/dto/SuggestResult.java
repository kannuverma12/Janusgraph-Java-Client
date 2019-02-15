package com.paytm.digital.education.explore.response.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SuggestResult {

    @JsonProperty("entity_id")
    private long entityId;

    @JsonProperty("official_name")
    private String officialName;

}
