package com.paytm.digital.education.explore.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseFeeDto {

    @JsonProperty("fee")
    private Integer fee;

    @JsonProperty("caste_group")
    private String casteGroup;
}
