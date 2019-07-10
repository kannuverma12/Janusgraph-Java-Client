package com.paytm.digital.education.explore.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.enums.Gender;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CutoffDto {

    @JsonProperty("caste_group")
    private String casteGroup;

    @JsonProperty("counselling")
    private CounsellingDto counselling;

    @JsonProperty("cutoff_type")
    private String cutoffType;

    @JsonProperty("exam_id")
    private long examId;

    @JsonProperty("final_value")
    private double finalValue;

    @JsonProperty("gender")
    private Gender gender;

    @JsonProperty("location")
    private String location;

    @JsonProperty("merit_list_type")
    private String meritListType;

    @JsonProperty("year")
    private int year;
}
