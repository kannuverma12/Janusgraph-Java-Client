package com.paytm.digital.education.explore.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RankingDto {

    @JsonProperty("year")
    private Integer year;

    @JsonProperty("rank")
    private Integer rank;

    @JsonProperty("score")
    private Double score;

    @JsonProperty("ranking_stream")
    private String rankingStream;

    @JsonProperty("category")
    private String category;

    @JsonProperty("source")
    private String source;

    @JsonProperty("ranking_type")
    private String rankingType;

    @JsonProperty("rating")
    private String rating;

}

