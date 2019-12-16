package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Placement implements Serializable {

    private static final long serialVersionUID = 9168032937699730589L;

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
