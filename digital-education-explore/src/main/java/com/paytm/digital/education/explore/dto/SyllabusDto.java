package com.paytm.digital.education.explore.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SyllabusDto {

    @JsonProperty("subject_name")
    private String subjectName;

    @JsonProperty("unit")
    private List<UnitDto> units;
}
