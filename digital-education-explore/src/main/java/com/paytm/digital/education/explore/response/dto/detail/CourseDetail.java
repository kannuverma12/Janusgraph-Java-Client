package com.paytm.digital.education.explore.response.dto.detail;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.response.dto.common.BannerData;
import com.paytm.digital.education.explore.response.dto.common.Widget;
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
    private Long instituteId;

    @JsonProperty("is_client")
    private Boolean isClient;

    @JsonProperty("institute")
    private CourseInstituteDetail institute;

    @JsonProperty("widgets")
    private List<Widget> widgets;

    @JsonProperty("banners")
    private List<BannerData> banners;

    @JsonProperty("derived_attributes")
    private Map<String, List<Attribute>> derivedAttributes;

    @JsonProperty("exams_accepted")
    private List<ExamDetail> examsAccepted;

}
