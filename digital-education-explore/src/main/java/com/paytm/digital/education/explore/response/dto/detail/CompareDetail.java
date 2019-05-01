package com.paytm.digital.education.explore.response.dto.detail;

import lombok.Data;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompareDetail {

    @JsonProperty("comparison_insights")
    private Map<String, String> comparisonSights;

    @JsonProperty("institutes")
    private List<CompareInstDetail> institutes;
}
