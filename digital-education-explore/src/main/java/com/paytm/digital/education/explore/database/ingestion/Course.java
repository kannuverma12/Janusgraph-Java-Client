package com.paytm.digital.education.explore.database.ingestion;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.enums.CourseLevel;
import com.paytm.digital.education.enums.PublishStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Course {

    @Id
    @Field("_id")
    @JsonIgnore
    private String id;

    @JsonProperty("id")
    @Field("course_id")
    private Long courseId;

    @JsonProperty("title")
    @Field("course_name_official")
    private String courseNameOfficial;

    @JsonProperty("url")
    @Field("url")
    private String url;

    @JsonProperty("official_brochure_url")
    @Field("official_brochure_url")
    private String officialBrochureUrl;

    @JsonProperty("about_course")
    @Field("about_course")
    private String aboutCourse;

    @JsonProperty("branch")
    @Field("master_branch")
    private String masterBranch;

    @JsonProperty("master_degree")
    @Field("master_degree")
    private List<String> masterDegree;

    @JsonProperty("course_duration")
    @Field("course_duration")
    private Integer courseDuration;

    @JsonProperty("seats_available")
    @Field("seats_available")
    private Integer seatsAvailable;

    @JsonProperty("study_mode")
    @Field("study_mode")
    private String studyMode;

    @JsonProperty("exams_accepted")
    @Field("exams_accepted")
    private List<Long> examsAccepted;

    @JsonProperty("course_fees")
    @Field("course_fees")
    private List<CourseFee> courseFees;

    @JsonProperty("fees_url_official")
    @Field("fees_url_official")
    private String feesUrlOfficial;

    @JsonProperty("institution_id")
    @Field("institute_id")
    private Long institutionId;

    @JsonProperty("form_application_url_officail")
    @Field("form_application_url_officail")
    private String formApplicationUrlOfficail;

    @JsonProperty("admission_process")
    @Field("admission_process")
    private String admissionProcess;

    @JsonProperty("admission_process_url_official")
    @Field("admission_process_url_official")
    public String admissionProcessUrlOfficial;

    @JsonProperty("eligibility_criteria")
    @Field("eligibility_criteria")
    private String eligibilityCriteria;

    @JsonProperty("eligibility_url_official")
    @Field("eligibility_url_official")
    private String eligibilityUrlOfficial;

    @JsonProperty("form_submission_date")
    @Field("form_submission_date")
    private FormSubmissionDate formSubmissionDate;

    @JsonProperty("last_updated")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Field("last_updated")
    private Date lastUpdated;

    @JsonProperty("course_level")
    @Field("course_level")
    private CourseLevel courseLevel;

    @JsonProperty("publishing_status")
    @Field("publishing_status")
    private PublishStatus publishingStatus;

    @JsonProperty("is_accepting_applications")
    @Field("is_accepting_applications")
    private boolean acceptingApplication;

    @Field("application_process_url_partner")
    @JsonProperty("application_process_url_partner")
    private String applicationProcessUrlPartner;

    @JsonProperty("streams")
    @Field("streams")
    private List<String> streams;

    @JsonProperty("cutoffs")
    @Field("cutoffs")
    private List<Cutoff> cutoffs;

    @JsonProperty("status")
    @Field("status")
    private String status;

}
