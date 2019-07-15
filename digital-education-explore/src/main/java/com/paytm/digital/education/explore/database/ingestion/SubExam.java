package com.paytm.digital.education.explore.database.ingestion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubExam {

    @JsonProperty("duration_hours")
    @Field("duration_hours")
    private Float durationHours;

    @JsonProperty("name")
    @Field("name")
    private String name;

    @JsonProperty("id")
    @Field("id")
    private Long id;

    @JsonProperty("instances")
    @Field("instances")
    private List<Instance> instances;

    @JsonProperty("sub_exam_name")
    @Field("sub_exam_name")
    private String subExamName;

    @JsonProperty("published_status")
    @Field("published_status")
    private String publishedStatus;

}
