package com.paytm.digital.education.explore.response.dto.detail;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.enums.Gender;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExamAndCutOff {

    @JsonProperty("exam_short_name")
    private String examShortName;

    @JsonProperty("exam_id")
    private Long examId;

    @JsonProperty("master_degree")
    private String masterDegree;

    @JsonProperty("has_cutoff")
    private Boolean hasCutoff;

    @JsonProperty("genders")
    private Map<Gender, String> genders;

    @JsonProperty("caste_groups")
    private Map<String, String> casteGroups;

}
