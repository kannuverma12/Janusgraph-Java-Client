package com.paytm.digital.education.coaching.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TopRankAchieved {

    @Field("exam_id")
    @JsonProperty("exam_id")
    private Integer examId;

    @Field("exam_name")
    @JsonProperty("exam_name")
    private String examName;

    @Field("rank_obtained")
    @JsonProperty("rank_obtained")
    private Integer rankObtained;

    @Field("student_count")
    @JsonProperty("student_count")
    private Integer studentCount;
}
