package com.paytm.digital.education.explore.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SyllabusDto {

    @JsonProperty("index")
    private Long index;

    @JsonProperty("subject_name")
    private String subjectName;

    @JsonProperty("unit")
    private List<UnitDto> units;

    @JsonProperty("subject_marks")
    private Double subjectMarks;
}
