package com.paytm.digital.education.explore.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubExamDto {

    @JsonProperty("duration_hours")
    private Float durationHours;

    @JsonProperty("name")
    private String name;

    @JsonProperty("id")
    private Long id;

    @JsonProperty("instances")
    private List<InstanceDto> instances;

    @JsonProperty("sub_exam_name")
    private String subExamName;

    @JsonProperty("published_status")
    private String publishedStatus;

}
