package com.paytm.digital.education.explore.database.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.enums.CourseLevel;
import com.paytm.digital.education.explore.enums.PublishStatus;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Data
@ToString
@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Course {

    @Field("admission_process_url_official")
    @JsonProperty("admission_process_url_official")
    public String admissionProcessUrlOfficial;

    @Id
    @Field("_id")
    @JsonIgnore
    private String id;

    @Field("course_id")
    @JsonProperty("course_id")
    private Long courseId;

    @Field("about_course")
    @JsonProperty("about_course")
    private String aboutCourse;

    @Field("master_branch")
    @JsonProperty("master_branch")
    private String masterBranch;

    @Field("master_degree")
    @JsonProperty("master_degree")
    private List<String> masterDegree;

    @Field("course_duration")
    @JsonProperty("course_duration")
    private Integer courseDuration;

    @Field("seats_available")
    @JsonProperty("seats_available")
    private Integer seatsAvailable;

    @Field("eligibility_url_official")
    @JsonProperty("eligibility_url_official")
    private String eligibilityUrlOfficial;

    @Field("exams_accepted")
    @JsonProperty("exams_accepted")
    private List<Long> examsAccepted;

    @Field("course_fees")
    @JsonProperty("course_fees")
    private List<CourseFee> courseFees;

    @Field("fees_url_official")
    @JsonProperty("fees_url_official")
    private String feesUrlOfficial;

    @Field("institute_id")
    @JsonProperty("institute_id")
    private Integer institutionId;

    @Field("form_application_url_officail")
    @JsonProperty("form_application_url_officail")
    private String formApplicationUrlOfficail;

    @Field("form_submission_date")
    @JsonProperty("form_submission_date")
    private FormSubmissionDate formSubmissionDate;

    @Field("last_updated")
    @JsonProperty("last_updated")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date lastUpdated;

    @Field("course_level")
    @JsonProperty("course_level")
    private CourseLevel courseLevel;

    @Field("publishing_status")
    @JsonProperty("publishing_status")
    private PublishStatus publishingStatus;

    @Field("streams")
    @JsonProperty("streams")
    private List<String> streams;

    @Field("course_name_official")
    @JsonProperty("course_name_official")
    private String courseNameOfficial;

    @Field("url")
    @JsonProperty("url")
    private String url;

    @Field("cutoffs")
    @JsonProperty("cutoffs")
    private List<Cutoff> cutoffs;
}
