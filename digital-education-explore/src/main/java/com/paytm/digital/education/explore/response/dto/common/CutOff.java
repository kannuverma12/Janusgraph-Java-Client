package com.paytm.digital.education.explore.response.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.enums.Gender;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CutOff {

    @JsonProperty("caste_group")
    private String casteGroup;

    @JsonProperty("cutoff_type")
    private String cutoffType;

    @JsonProperty("exam_id")
    private long examId;

    @JsonProperty("value")
    private double value;

    @JsonProperty("gender")
    private Gender gender;

    @JsonProperty("merit_list_type")
    private String meritListType;

    @JsonProperty("location")
    private String location;

    @JsonProperty("year")
    private int year;
}
