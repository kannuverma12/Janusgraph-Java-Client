package com.paytm.digital.education.explore.response.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AutoSuggestResponse {

    @JsonProperty("meta")
    private AutoSuggestMeta meta;

    @JsonProperty("data")
    private List<AutoSuggestData> data;

    public AutoSuggestResponse(String searchTerm) {
        this.meta = new AutoSuggestMeta(searchTerm);
    }
}
