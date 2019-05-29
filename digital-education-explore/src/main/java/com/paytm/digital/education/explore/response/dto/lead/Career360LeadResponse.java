package com.paytm.digital.education.explore.response.dto.lead;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Career360LeadResponse extends BaseLeadResponse {

    @JsonProperty("error_code")
    private Integer errorCode;

    @JsonProperty("message")
    private String message;

    @JsonProperty("cta_status")
    private Integer ctaStatus;

    @JsonProperty("cta_message")
    private String ctaMessage;

}
