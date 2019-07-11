package com.paytm.digital.education.explore.database.ingestion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.paytm.digital.education.explore.enums.PublishStatus;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Exam {

    @Id
    @Field("_id")
    @JsonIgnore
    private String id;

    @Field("conducting_body")
    @JsonProperty("conducting_body")
    private String conductingBody;

    @Field("exam_category")
    @JsonProperty("exam_category")
    private String examCategory;

    @Field("about_exam")
    @JsonProperty("about_exam")
    private String aboutExam;

    @Field("documents_counselling")
    @JsonProperty("documents_counselling")
    private List<String> documentsCounselling;

    @Field("documents_exam")
    @JsonProperty("documents_exam")
    private List<String> documentsExam;

    @Field("frequency_of_conduct")
    @JsonProperty("frequency_of_conduct")
    private Integer frequencyOfConduct;

    @Field("exam_id")
    @JsonProperty("id")
    private Long examId;

    @Field("instances")
    @JsonProperty("instances")
    private List<Instance> instances;

    @Field("linguistic_medium_exam")
    @JsonProperty("linguistic_medium_exam")
    private List<String> linguisticMediumExam;

    @Field("level_of_exam")
    @JsonProperty("level_of_exam")
    private String levelOfExam;

    @Field("exam_full_name")
    @JsonProperty("exam_full_name")
    private String examFullName;

    @Field("exam_short_name")
    @JsonProperty("exam_short_name")
    private String examShortName;

    @Field("synonyms")
    @JsonProperty("synonyms")
    private List<String> synonyms;

    @Field("contact_number")
    @JsonProperty("contact_number")
    private String contactNumber;

    @Field("published_status")
    @JsonProperty("published_status")
    private PublishStatus publishedStatus;

    @Field("subexams")
    @JsonProperty("subexams")
    private ArrayList<SubExam> subExams;

    @Field("official_url")
    @JsonProperty("official_url")
    private String officialUrl;

    @Field("exam_duration")
    @JsonProperty("duration_hours")
    private Float examDuration;

    @JsonProperty("syllabus")
    @Field("syllabus")
    private List<Syllabus> syllabus;

    @Field("domains")
    @JsonProperty("domains")
    private List<String> domains;

    @Field("logo")
    @JsonProperty("logo")
    private String logo;

    @Field("application_fees")
    @JsonProperty("application_fees")
    private List<ApplicationFee> applicationFees;

    @Field("last_updated")
    @JsonProperty("last_updated")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date lastUpdated;

    @Field("status")
    @JsonProperty("status")
    private String status;
}