package com.paytm.digital.education.coaching.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SuccessStory {

    @Field("student_name")
    @JsonProperty("student_name")
    private String studentName;

    @Field("photo")
    @JsonProperty("photo")
    private String photo;

    @Field("year")
    @JsonProperty("year")
    private int year;

    @Field("college_achieved")
    @JsonProperty("college_achieved")
    private String collegeObtained;

    @Field("rank_obtained")
    @JsonProperty("rank_obtained")
    private Integer rankObtained;

    @Field("qualified_exam")
    @JsonProperty("qualified_exam")
    private String qualifiedExam;

}
