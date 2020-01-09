package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.enums.CourseLevel;
import com.paytm.digital.education.enums.PublishStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ToString
@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class Course implements Serializable {

    private static final long serialVersionUID = -7263845681788980171L;

    @Id
    @Field("_id")
    @JsonIgnore
    private String id;

    @Field("course_id")
    private Long courseId;

    @Field("course_name_official")
    private String courseNameOfficial;

    @Field("url")
    private String url;

    @Field("official_brochure_url")
    private String officialBrochureUrl;

    @Field("about_course")
    private String aboutCourse;

    @Field("master_branch")
    private String masterBranch;

    @Field("master_degree")
    private List<String> masterDegree;

    @Field("course_duration")
    private Integer courseDuration;

    @Field("seats_available")
    private Integer seatsAvailable;

    @Field("study_mode")
    private String studyMode;

    @Field("exams_accepted")
    private List<Long> examsAccepted;

    @Field("course_fees")
    private List<CourseFee> courseFees;

    @Field("fees_url_official")
    private String feesUrlOfficial;

    @Field("institute_id")
    private Long institutionId;

    @Field("form_application_url_officail")
    private String formApplicationUrlOfficail;

    @Field("admission_process")
    private String admissionProcess;

    @Field("admission_process_url_official")
    public String admissionProcessUrlOfficial;

    @Field("eligibility_criteria")
    private String eligibilityCriteria;

    @Field("eligibility_url_official")
    private String eligibilityUrlOfficial;

    @Field("form_submission_date")
    private FormSubmissionDate formSubmissionDate;

    @Field("last_updated")
    private Date lastUpdated;

    @Field("course_level")
    private CourseLevel courseLevel;

    @Field("publishing_status")
    private PublishStatus publishingStatus;

    @Field("is_accepting_applications")
    private boolean acceptingApplication;

    @Field("streams")
    private List<String> streams;

    @Field("cutoffs")
    private List<Cutoff> cutoffs;

    @Field("application_process_url_partner")
    private String applicationProcessUrlPartner;

    @Field("status")
    private String status;

    @Field("stream_ids")
    private List<Long> streamIds;

    @Field("lead_enabled")
    private int leadEnabled;
}
