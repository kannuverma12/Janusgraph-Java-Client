package com.paytm.digital.education.explore.request.dto.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.enums.Gender;
import com.paytm.digital.education.explore.sro.request.FieldsAndFieldGroupRequest;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CutoffSearchRequest extends FieldsAndFieldGroupRequest {

    @JsonProperty("institute_id")
    @Min(1)
    private long instituteId;

    @JsonProperty("exam_id")
    @Min(1)
    private long examId;

    @JsonProperty("gender")
    @NotNull
    private Gender gender;

    @JsonProperty("caste_group")
    @NotNull
    private String casteGroup;
}
