package com.paytm.digital.education.database.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document
public class ExamLogo {

    @Field("exam_id")
    private Long examId;

    @Id
    @Field("_id")
    private String id;

    @Field("logo")
    private String logo;

}
