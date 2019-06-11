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

    @Field("name")
    @JsonProperty("name")
    private String name;

    @Field("photo")
    @JsonProperty("photo")
    private String photo;

    @Field("course_studied")
    @JsonProperty("course_studied")
    private String courseStudied;

    @Field("year")
    @JsonProperty("year")
    private Integer year;

    @Field("batch")
    @JsonProperty("batch")
    private String batch;

    @Field("rank_obtained")
    @JsonProperty("rank_obtained")
    private Integer rankObtained;

    @Field("qualified_exam")
    @JsonProperty("qualified_exam")
    private String qualifiedExam;

    @Field("exam_year")
    @JsonProperty("exam_year")
    private Integer examYear;

    @Field("college_obtained")
    @JsonProperty("college_obtained")
    private String collegeObtained;
}
