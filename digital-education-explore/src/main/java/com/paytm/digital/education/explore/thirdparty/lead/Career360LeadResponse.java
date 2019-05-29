package com.paytm.digital.education.explore.thirdparty.lead;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Career360LeadResponse {

    @JsonProperty("error_code")
    private Integer errorCode;

    @JsonProperty("message")
    private String message;

    @JsonProperty("cta_status")
    private Integer ctaStatus;

    @JsonProperty("cta_message")
    private String ctaMessage;
}
