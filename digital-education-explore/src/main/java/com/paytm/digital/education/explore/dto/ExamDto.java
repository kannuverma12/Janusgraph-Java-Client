package com.paytm.digital.education.explore.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Field;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.paytm.digital.education.explore.enums.PublishStatus;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExamDto {

    @JsonProperty("conducting_body")
    private String conductingBody;

    @JsonProperty("exam_category")
    private String examCategory;

    @JsonProperty("about_exam")
    private String aboutExam;

    @JsonProperty("documents_counselling")
    private List<String> documentsCounselling;

    @JsonProperty("documents_exam")
    private List<String> documentsExam;

    @JsonProperty("frequency_of_conduct")
    private Integer frequencyOfConduct;

    @JsonProperty("id")
    private Long examId;

    @JsonProperty("instances")
    private List<InstanceDto> instances;

    @JsonProperty("linguistic_medium_exam")
    private List<String> linguisticMediumExam;

    @JsonProperty("level_of_exam")
    private String levelOfExam;

    @JsonProperty("exam_full_name")
    private String examFullName;

    @JsonProperty("exam_short_name")
    private String examShortName;

    @JsonProperty("synonyms")
    private List<String> synonyms;

    @JsonProperty("contact_number")
    private String contactNumber;

    @JsonProperty("published_status")
    private PublishStatus publishedStatus;

    @JsonProperty("subexams")
    private ArrayList<SubExamDto> subExams;

    @JsonProperty("official_url")
    private String officialUrl;

    @JsonProperty("duration_hours")
    private Float examDuration;

    @JsonProperty("syllabus")
    @Field("syllabus")
    private List<SyllabusDto> syllabus;

    @JsonProperty("domains")
    private List<String> domains;

    @JsonProperty("logo")
    private String logo;

    @JsonProperty("application_fees")
    private List<ApplicationFeeDto> applicationFees;

    @JsonProperty("last_updated")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date lastUpdated;

    @JsonProperty("status")
    private String status;
}
