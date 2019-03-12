package com.paytm.digital.education.explore.response.dto.detail;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Ranking {

    @JsonProperty("rank")
    public Integer rank;

    @JsonProperty("score")
    public Double score;

    @JsonProperty("stream")
    public String stream;

    @JsonProperty("year")
    public Integer year;

    @JsonProperty("category")
    public String category;
}
