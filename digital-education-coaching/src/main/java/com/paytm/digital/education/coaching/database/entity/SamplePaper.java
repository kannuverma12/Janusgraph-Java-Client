package com.paytm.digital.education.coaching.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SamplePaper {

    @Field("is_paid")
    @JsonProperty("is_paid")
    private Boolean isPaid;

    @Field("is_solved")
    @JsonProperty("is_solved")
    private Boolean isSolved;

    @Field("year")
    @JsonProperty("year")
    private Integer year;

    @Field("url")
    @JsonProperty("url")
    private String url;

}
