package com.paytm.digital.education.coaching.consumer.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.enums.CourseType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TopCoachingCoursesForExam {

    @JsonProperty("course_id")
    private Long courseId;

    @JsonProperty("coaching_institute_id")
    private Long coachingInstituteId;

    @JsonProperty("coaching_institute_name")
    private String coachingInstituteName;

    @JsonProperty("logo")
    private String logo;

    @JsonProperty("course_name")
    private String courseName;

    @JsonProperty("eligibility")
    private String eligibility;

    @JsonProperty("duration_months")
    private String durationMonths;

    @JsonProperty("course_type")
    private CourseType courseType;

    @JsonProperty("url_display_key")
    private String urlDisplayKey;
}
