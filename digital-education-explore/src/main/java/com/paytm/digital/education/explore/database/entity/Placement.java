package com.paytm.digital.education.explore.database.entity;

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
    @JsonProperty("year")
    private Integer year;

    @Field("degree")
    @JsonProperty("degree")
    private String degree;

    @Field("median")
    @JsonProperty("median")
    private Integer median;

}
