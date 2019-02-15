package com.paytm.digital.education.explore.response.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AutoSuggestMeta {

    @JsonProperty("term")
    private String term;

}
