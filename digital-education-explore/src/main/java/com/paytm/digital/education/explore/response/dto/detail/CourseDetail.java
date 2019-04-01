package com.paytm.digital.education.explore.response.dto.detail;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseDetail {

    @JsonProperty("course_id")
    private Long courseId;

    @JsonProperty("course_name_official")
    private String courseNameOfficial;

    @JsonProperty("official_brochure_url")
    private String officialBrochureUrl;

    @JsonProperty("course_duration")
    private Integer courseDuration;

    @JsonProperty("seats_available")
    private Integer seatsAvailable;

    @JsonProperty("study_mode")
    private String studyMode;

    @JsonProperty("course_level")
    private String courseLevel;

    @JsonProperty("course_fees")
    private List<CourseFee> courseFees;

    @JsonProperty("fees_url_official")
    private String feesUrlOfficial;

    @JsonProperty("about_course")
    private String aboutCourse;

    @JsonProperty("admission_process")
    private String admissionProcess;

    @JsonProperty("admission_process_url_official")
    private String admissionProcessUrlOfficial;

    @JsonProperty("eligibility_criteria")
    private String eligibilityCriteria;

    @JsonProperty("eligibility_url_official")
    private String eligibilityUrlOfficial;

    @JsonProperty("institute_id")
    private Integer instituteId;

    @JsonProperty("institute")
    private CourseInstituteDetail institute;

    @JsonProperty("derived_attributes")
    private Map<String, List<Attribute>> derivedAttributes;

}
