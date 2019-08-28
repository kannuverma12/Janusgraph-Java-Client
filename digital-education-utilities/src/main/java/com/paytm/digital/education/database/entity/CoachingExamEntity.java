package com.paytm.digital.education.database.entity;

import com.paytm.digital.education.enums.ExamType;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Document("coaching_exam")
public class CoachingExamEntity {

    @Id
    @Field("_id")
    ObjectId id;

    @Indexed(unique = true)
    @Field("exam_id")
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
    private Long courseId;

    @Field("stream_id")
    private Long streamId;

    @Field("exam_duration")
    private String examDuration;

    @Field("maximum_marks")
    private Double maximumMarks;

    @Field("exam_date")
    private List<LocalDateTime> examDate;

    @Field("eligibility")
    private String eligibility;

    @Field("active")
    private boolean active;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;
}
