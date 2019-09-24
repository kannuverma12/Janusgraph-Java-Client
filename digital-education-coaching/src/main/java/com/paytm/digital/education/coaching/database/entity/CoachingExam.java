package com.paytm.digital.education.coaching.database.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.coaching.response.dto.ResponseDto;
import com.paytm.digital.education.enums.ExamType;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.YYYY_MM_DD;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.YYYY_MM_DD_T_HH_MM_SS;

@Data
@Document("coaching_exam")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoachingExam extends ResponseDto {

    @Field("_id")
    @JsonIgnore
    ObjectId id;

    @Field("exam_id")
    @JsonProperty("exam_id")
    private Long examId;

    @Field("institute_id")
    @JsonProperty("institute_id")
    private Long instituteId;

    @Field("exam_name")
    @JsonProperty("exam_name")
    private String examName;

    @Field("exam_description")
    @JsonProperty("exam_description")
    private String examDescription;

    //    @Field("exam_dates")
    //    @JsonProperty("exams_dates")
    //    private Map<String, ExamDate> examDates;
    @Field("exam_date")
    @JsonProperty("exam_date")
    @JsonFormat(pattern = YYYY_MM_DD)
    private List<Date> examDates;

    @Field("course_id")
    @JsonProperty("course_id")
    private Long courseId;

    @Field("exam_type")
    @JsonProperty("exam_type")
    private ExamType examType;

    @Field("marks")
    @JsonProperty("marks")
    private Double marks;

    @Field("exam_duration")
    @JsonProperty("exam_duration")
    private Integer examDuration;

    @Field("active")
    @JsonProperty("active")
    private Boolean active;

    @Field("created_at")
    @JsonProperty("created_at")
    @JsonFormat(pattern = YYYY_MM_DD_T_HH_MM_SS)
    private Date createdAt;

    @Field("updated_at")
    @JsonProperty("updated_at")
    @JsonFormat(pattern = YYYY_MM_DD_T_HH_MM_SS)
    private Date updatedAt;
}
