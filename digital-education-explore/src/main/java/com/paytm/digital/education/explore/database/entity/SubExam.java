package com.paytm.digital.education.explore.database.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.enums.PublishStatus;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubExam {

    @Field("durationHours")
    @JsonProperty("duration_hours")
    private Double durationHours;

    @Field("id")
    @JsonProperty("id")
    private Integer id;

    @Field("instances")
    @JsonProperty("instances")
    private List<Instance> instances;

    @Field("sub_exam_name")
    @JsonProperty("sub_exam_name")
    private String subExamName;

    @Field("publishedStatus")
    @JsonProperty("published_status")
    private PublishStatus publishedStatus;
}
