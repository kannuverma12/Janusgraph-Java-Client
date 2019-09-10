package com.paytm.digital.education.database.entity;

import com.paytm.digital.education.enums.StudentCategory;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "top_ranker")
public class TopRankerEntity extends Base {

    @Id
    private ObjectId id;

    @Field("top_ranker_id")
    @Indexed(unique = true)
    // todo: check to port same in production
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

    @Field("course_ids")
    private List<Long> courseIds;

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

    @Field("student_category")
    private StudentCategory studentCategory;

    @Field("testimonial")
    private String testimonial;
}
