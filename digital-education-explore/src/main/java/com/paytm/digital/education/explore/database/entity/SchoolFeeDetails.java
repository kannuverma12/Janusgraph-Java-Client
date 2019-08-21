package com.paytm.digital.education.explore.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SchoolFeeDetails {

    @JsonProperty("fees")
    private Long feeAmount;

    @JsonProperty("fees_tenure")
    private String feeTenure;
}
