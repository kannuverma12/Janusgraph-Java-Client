package com.paytm.digital.education.explore.database.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.enums.PublishStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class Exam {

    @Id
    @Field("_id")
    @JsonIgnore
    private String id;

    @Field("exam_id")
    @JsonProperty("exam_id")
    private Long examId;

    @Field("about_exam")
    @JsonProperty("about_exam")
    private String aboutExam;

    @Field("conducting_body")
    @JsonProperty("conducting_body")
    private String conductingBody;

    @Field("contact_number")
    @JsonProperty("contact_number")
    private String contactNumber;

    @Field("documentsCounselling")
    @JsonProperty("documents_counselling")
    private List<String> documentsCounselling;

    @Field("documentsExam")
    @JsonProperty("documents_exam")
    private List<String> documentsExam;

    @Field("exam_category")
    @JsonProperty("exam_category")
    private String examCategory;

    @Field("exam_duration")
    @JsonProperty("exam_duration")
    private Double examDuration;

    @Field("exam_full_name")
    @JsonProperty("exam_full_name")
    private String examFullName;

    @Field("exam_short_name")
    @JsonProperty("exam_short_name")
    private String examShortName;

    @Field("frequency_of_conduct")
    @JsonProperty("frequency_of_conduct")
    private Integer frequencyOfConduct;

    @Field("instances")
    @JsonProperty("instances")
    private List<Instance> instances;

    @Field("lastUpdated")
    @JsonProperty("last_updated")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date lastUpdated;

    @Field("level_of_exam")
    @JsonProperty("level_of_exam")
    private String levelOfExam;

    @Field("linguistic_medium_exam")
    @JsonProperty("linguistic_medium_exam")
    private List<String> linguisticMediumExam;

    @Field("official_url")
    @JsonProperty("official_url")
    private String officialUrl;

    @Field("publishedStatus")
    @JsonProperty("published_status")
    private PublishStatus publishedStatus;

    @Field("subexams")
    @JsonProperty("subexams")
    private ArrayList<SubExam> subExams;

    @Field("synonyms")
    @JsonProperty("synonyms")
    private List<String> synonyms;

    public Exam(long examId) {
        this.examId = examId;
    }

    public Exam(String examShortName, Long examId) {
        this.examShortName = examShortName;
        this.examId = examId;
    }
}
