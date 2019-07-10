package com.paytm.digital.education.explore.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.enums.CourseLevel;
import com.paytm.digital.education.explore.enums.PublishStatus;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseDto {

    @JsonProperty("id")
    private Long courseId;

    @JsonProperty("title")
    private String courseNameOfficial;

    @JsonProperty("url")
    private String url;

    @JsonProperty("official_brochure_url")
    private String officialBrochureUrl;

    @JsonProperty("about_course")
    private String aboutCourse;

    @JsonProperty("branch")
    private String masterBranch;

    @JsonProperty("master_degree")
    private List<String> masterDegree;

    @JsonProperty("course_duration")
    private Integer courseDuration;

    @JsonProperty("seats_available")
    private Integer seatsAvailable;

    @JsonProperty("study_mode")
    private String studyMode;

    @JsonProperty("exams_accepted")
    private List<Long> examsAccepted;

    @JsonProperty("course_fees")
    private List<CourseFeeDto> courseFees;

    @JsonProperty("fees_url_official")
    private String feesUrlOfficial;

    @JsonProperty("institution_id")
    private Long institutionId;

    @JsonProperty("form_application_url_officail")
    private String formApplicationUrlOfficail;

    @JsonProperty("admission_process")
    private String admissionProcess;

    @JsonProperty("admission_process_url_official")
    public String admissionProcessUrlOfficial;

    @JsonProperty("eligibility_criteria")
    private String eligibilityCriteria;

    @JsonProperty("eligibility_url_official")
    private String eligibilityUrlOfficial;

    @JsonProperty("form_submission_date")
    private FormSubmissionDateDto formSubmissionDate;

    @JsonProperty("last_updated")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date lastUpdated;

    @JsonProperty("course_level")
    private CourseLevel courseLevel;

    @JsonProperty("publishing_status")
    private PublishStatus publishingStatus;

    @JsonProperty("is_accepting_applications")
    private boolean acceptingApplication;

    @JsonProperty("application_process_url_partner")
    private String applicationProcessUrlPartner;

    @JsonProperty("streams")
    private List<String> streams;

    @JsonProperty("cutoffs")
    private List<CutoffDto> cutoffs;

    @JsonProperty("status")
    private String status;

}
