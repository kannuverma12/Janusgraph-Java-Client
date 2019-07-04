package com.paytm.digital.education.coaching.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StudentSelected {

    @Field("institute_id")
    @JsonProperty("institute_id")
    private Long instituteId;

    @Field("qualifying_exam_id")
    @JsonProperty("qualifying_exam_id")
    private Long qualifyingExamId;

    @Field("exam_year")
    @JsonProperty("exam_year")
    private Integer examYear;

    @Field("student_name")
    @JsonProperty("student_name")
    private String studentName;

    @Field("student_photo")
    @JsonProperty("student_photo")
    private String studentPhoto;

    @Field("course_studied")
    @JsonProperty("course_studied")
    private String courseStudied;

    @Field("batch")
    @JsonProperty("batch")
    private String batch;

    @Field("rank_obtained")
    @JsonProperty("rank_obtained")
    private Integer rankObtained;

    @Field("college_admitted")
    @JsonProperty("college_admitted")
    private String collegeAdmitted;
}
