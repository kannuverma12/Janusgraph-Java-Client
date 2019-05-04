package com.paytm.digital.education.explore.response.dto.detail;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.enums.CourseLevel;
import com.paytm.digital.education.explore.response.dto.common.CutOff;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompareCourseDetail {

    @JsonProperty("course_id")
    private Long courseId;

    @JsonProperty("course_name")
    private String courseName;

    @JsonProperty("course_fee")
    private Integer courseFee;

    @JsonProperty("total_intake")
    private Integer totalIntake;

    @JsonProperty("course_level")
    private CourseLevel courseLevel;

    @JsonProperty("course_duration_in_months")
    private Integer courseDurationInMonths;

    @JsonProperty("eligibility_criteria")
    private String eligibilityCriteria;

    @JsonProperty("exams_accepted")
    private List<String> examsAccepted;

    @JsonProperty("cutoffs")
    private List<CutOff> cutoffs;
}

