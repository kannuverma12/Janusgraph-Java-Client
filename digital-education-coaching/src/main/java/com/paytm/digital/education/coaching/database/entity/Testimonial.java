package com.paytm.digital.education.coaching.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Testimonial {

    @Field("student_name")
    @JsonProperty("student_name")
    private String studentName;

    @Field("student_batch")
    @JsonProperty("student_batch")
    private String studentBatch;

    @Field("photo")
    @JsonProperty("photo")
    private String photo;

    @Field("signature")
    @JsonProperty("signature")
    private String signature;

    @Field("testimonial")
    @JsonProperty("testimonial")
    private String testimonial;

    @Field("college_obtained")
    @JsonProperty("college_obtained")
    private String collegeObtained;

    @Field("rank_obtained")
    @JsonProperty("rank_obtained")
    private Integer rankObtained;

    @Field("qualified_exam")
    @JsonProperty("qualified_exam")
    private String qualifiedExam;
}
