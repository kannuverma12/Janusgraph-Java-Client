package com.paytm.digital.education.dto.detail;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.enums.Gender;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExamAndCutOff implements Serializable {

    private static final long serialVersionUID = -2345675229431173057L;

    @JsonProperty("exam_short_name")
    private String examShortName;

    @JsonProperty("exam_id")
    private Long examId;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("url_display_key")
    private String urlDisplayKey;

    @JsonProperty("master_degree")
    private String masterDegree;

    @JsonProperty("has_cutoff")
    private Boolean hasCutoff;

    @JsonProperty("genders")
    private Map<Gender, String> genders;

    @JsonProperty("caste_groups")
    private Map<String, String> casteGroups;

}
