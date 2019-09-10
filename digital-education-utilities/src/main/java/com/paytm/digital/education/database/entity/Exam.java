package com.paytm.digital.education.database.entity;

import com.paytm.digital.education.enums.PublishStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "exam")
public class Exam extends Base {

    @Id
    @Field("_id")
    private String id;

    @Field("conducting_body")
    private String conductingBody;

    @Field("exam_category")
    private String examCategory;

    @Field("about_exam")
    private String aboutExam;

    @Field("documents_counselling")
    private List<String> documentsCounselling;

    @Field("documents_exam")
    private List<String> documentsExam;

    @Field("frequency_of_conduct")
    private Integer frequencyOfConduct;

    @Field("exam_id")
    private Long examId;

    @Field("instances")
    private List<Instance> instances;

    @Field("linguistic_medium_exam")
    private List<String> linguisticMediumExam;

    @Field("level_of_exam")
    private String levelOfExam;

    @Field("exam_full_name")
    private String examFullName;

    @Field("exam_short_name")
    private String examShortName;

    @Field("synonyms")
    private List<String> synonyms;

    @Field("contact_number")
    private String contactNumber;

    @Field("published_status")
    private PublishStatus publishedStatus;

    @Field("subexams")
    private ArrayList<SubExam> subExams;

    @Field("official_url")
    private String officialUrl;

    @Field("exam_duration")
    private Float examDuration;

    @Field("syllabus")
    private List<Syllabus> syllabus;

    @Field("domains")
    private List<String> domains;

    @Field("logo")
    private String logo;

    @Field("application_fees")
    private List<ApplicationFee> applicationFees;

    @Field("last_updated")
    private Date lastUpdated;

    @Field("status")
    private String status;

    @Field("stream_ids")
    private List<Long> streamIds;

    public Exam(String examShortName, Long examId) {
        this.examShortName = examShortName;
        this.examId = examId;
    }
}
