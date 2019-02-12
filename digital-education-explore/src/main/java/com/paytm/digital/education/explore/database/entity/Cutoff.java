package com.paytm.digital.education.explore.database.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Cutoff {

    @Field("caste_group")
    @JsonProperty("caste_group")
    private String casteGroup;

    @Field("counselling")
    @JsonProperty("counselling")
    private Counselling counselling;

    @Field("cutoff_type")
    @JsonProperty("cutoff_type")
    private String cutoffType;

    @Field("exam_id")
    @JsonProperty("exam_id")
    private int examId;

    @Field("final_value")
    @JsonProperty("final_value")
    private double finalValue;

    @Field("gender")
    @JsonProperty("gender")
    private String gender;

    @Field("merit_list_type")
    @JsonProperty("merit_list_type")
    private String meritListType;

    @Field("year")
    @JsonProperty("year")
    private int year;
}
