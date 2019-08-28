package com.paytm.digital.education.database.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "top_ranker")
public class TopRankerEntity {

    @Id
    private ObjectId id;

    @Field("top_ranker_id")
    private Long topRankerId;

    @Field("institute_id")
    private Long instituteId;

    @Field("center_id")
    private Long centerId;

    @Field("exam_id")
    private Long examId;

    @Field("student_name")
    private String studentName;

    @Field("student_photo")
    private String studentPhoto;

    @Field("program_id")
    private Long programId;

    @Field("year")
    private String year;

    @Field("batch")
    private String batch;

    @Field("rank_obtained")
    private String rankObtained;

    @Field("exam_year")
    private String examYear;

    @Field("college_admitted")
    private String collegeAdmitted;

    @Field("testimonial")
    private String testimonial;
}
