package com.paytm.digital.education.database.entity;

import com.paytm.digital.education.enums.ExamType;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Data
@NoArgsConstructor
@Document("coaching_exam")
public class CoachingExamEntity extends Base {

    @Id
    @Field("_id")
    @JsonIgnore
    private ObjectId id;

    @Indexed(unique = true)
    @Field("coaching_exam_id")
    private Long coachingExamId;

    @Field("institute_id")
    private Long instituteId;

    @Field("exam_type")
    private ExamType examType;

    @Field("exam_name")
    private String examName;

    @Field("exam_description")
    private String examDescription;

    @Field("course_id")
    private List<Long> courseIds;

    @Field("stream_id")
    private List<Long> streamIds;

    @Field("exam_duration")
    private String examDuration;

    @Field("maximum_marks")
    private Double maximumMarks;

    @Field("exam_date")
    private LocalDateTime examDate;

    @Field("eligibility")
    private String eligibility;

}

