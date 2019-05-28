package com.paytm.digital.education.explore.response.dto.lead;

import com.fasterxml.jackson.annotation.JsonProperty;

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
