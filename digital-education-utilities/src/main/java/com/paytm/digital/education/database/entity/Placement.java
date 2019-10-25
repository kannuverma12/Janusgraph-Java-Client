package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Placement {

    @Field("year")
    private Integer year;

    @Field("degree")
    private String degree;

    @Field("median")
    private Integer median;

    @Field("average")
    public Integer average;

    @Field("maximum")
    public Integer maximum;

    @Field("minimum")
    public Integer minimum;
}
