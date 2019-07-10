package com.paytm.digital.education.explore.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InstanceDto {

    @JsonProperty("admission_year")
    private Integer admissionYear;

    @JsonProperty("exam_centers")
    private List<String> examCenters;

    @JsonProperty("events")
    private List<EventDto> events;

    @JsonProperty("instance_id")
    private Integer instanceId;

    @JsonProperty("instance_name")
    private String instanceName;

    @JsonProperty("parent_instance_id")
    private Integer parentInstanceId;

    @JsonProperty("eligibility_criteria")
    private String eligibilityCriteria;

    @JsonProperty("syllabus")
    private List<SyllabusDto> syllabusList;

    @JsonProperty("pattern")
    private String pattern;
}
