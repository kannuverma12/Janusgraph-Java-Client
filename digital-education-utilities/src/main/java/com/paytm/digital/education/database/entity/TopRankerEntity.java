package com.paytm.digital.education.database.entity;

import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Builder
@Document(collection = "top_ranker")
public class TopRankerEntity extends Base {

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

    @Field("exam_name")
    private String examName;

    @Field("student_name")
    private String studentName;

    @Field("student_photo")
    private String studentPhoto;

    @Field("course_ids")
    private List<Long> courseIds;

    @Field("course_names")
    private List<String> courseNames;

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
