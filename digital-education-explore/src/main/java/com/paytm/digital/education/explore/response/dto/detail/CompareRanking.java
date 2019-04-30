package com.paytm.digital.education.explore.response.dto.detail;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompareRanking {
    @JsonProperty("title")
    public String title;

    @JsonProperty("subtitle")
    public String subtitle;

    @JsonProperty("source")
    public String source;
}
