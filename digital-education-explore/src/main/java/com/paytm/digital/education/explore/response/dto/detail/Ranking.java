package com.paytm.digital.education.explore.response.dto.detail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties( { "label", "institute_id" })
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

    @JsonProperty("source")
    public String source;

    @JsonProperty("ranking_type")
    private String rankingType;

    @JsonProperty("rating")
    private String rating;

    @JsonProperty("label")
    private String label;

    @JsonProperty("institute_id")
    private Long instituteId;

}
