package com.paytm.digital.education.explore.response.dto.detail;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.enums.Gender;
import lombok.Data;

import java.util.List;

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
    private List<Gender> genders;

    @JsonProperty("caste_groups")
    private List<String> casteGroups;

}
