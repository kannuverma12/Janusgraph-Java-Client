package com.paytm.digital.education.coaching.googlesheet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CoachingCourseForm {
    @JsonProperty("course_id")
    private Long courseId;

    @JsonProperty("institute_id")
    private Long instituteId;

    @JsonProperty("course_name")
    private String courseName;

    @JsonProperty("course_category")
    private String courseCategory;

    @JsonProperty("stream_prepared_for")
    private String streamPreparedFor;

    @JsonProperty("exams_prepared_for")
    private String examsPreparedFor;

    @JsonProperty("course_duration_in_months")
    private Integer courseDurationInMonths;

    @JsonProperty("eligibility_criteria")
    private String eligibilityCriteria;

    @JsonProperty("course_description")
    private String courseDescription;

    @JsonProperty("course_intro_(_about_)")
    private String courseDetails;

    @JsonProperty("class_schedule")
    private String classSchedule;

    @JsonProperty("study_material_description")
    private String studyMaterialDescription;

    @JsonProperty("teaching_methodology")
    private String teachingMethodology;

    @JsonProperty("status")
    private String status;

    @JsonProperty("facilities_available")
    private String facilities;
}
