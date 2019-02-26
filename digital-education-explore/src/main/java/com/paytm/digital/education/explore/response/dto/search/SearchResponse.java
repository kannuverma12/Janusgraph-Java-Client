package com.paytm.digital.education.explore.response.dto.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchResponse {

    @JsonProperty("results")
    private SearchResult results;

    @JsonProperty("filters")
    private List<FilterData> filters;

    @JsonProperty("total")
    private long total;
}
