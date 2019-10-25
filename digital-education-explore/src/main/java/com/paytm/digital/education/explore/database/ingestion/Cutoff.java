package com.paytm.digital.education.explore.database.ingestion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
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
    private long examId;

    @Field("final_value")
    @JsonProperty("final_value")
    private double finalValue;

    @Field("gender")
    @JsonProperty("gender")
    private String gender;

    @Field("location")
    @JsonProperty("location")
    private String location;

    @Field("merit_list_type")
    @JsonProperty("merit_list_type")
    private String meritListType;

    @Field("year")
    @JsonProperty("year")
    private int year;
}
