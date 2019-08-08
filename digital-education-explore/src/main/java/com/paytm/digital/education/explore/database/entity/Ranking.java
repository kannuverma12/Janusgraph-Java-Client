package com.paytm.digital.education.explore.database.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Ranking {

    @Field("year")
    @JsonProperty("year")
    private Integer year;

    @Field("rank")
    @JsonProperty("rank")
    private Integer rank;

    @Field("score")
    @JsonProperty("score")
    private Double score;

    @Field("stream")
    @JsonProperty("stream")
    private String stream;

    @Field("rankingStream")
    @JsonProperty("rankingStream")
    private String rankingStream;

    @Field("category")
    @JsonProperty("category")
    private String category;

    @Field("source")
    @JsonProperty("source")
    private String source;

    @Field("logo")
    @JsonProperty("logo")
    private String logo;

    @Field("ranking_type")
    @JsonProperty("ranking_type")
    private String rankingType;

    @Field("rating")
    @JsonProperty("rating")
    private String rating;

}
