package com.paytm.digital.education.explore.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationFeeDto {
    @JsonProperty("category")
    private String category;

    @JsonProperty("quota")
    private String quota;

    @JsonProperty("mode")
    private String mode;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("amount")
    private Double amount;
}
