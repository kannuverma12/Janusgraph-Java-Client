package com.paytm.digital.education.explore.thirdparty.lead;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Career360UnfollowResponse {

    @JsonProperty("error_code")
    private Integer errorCode;

    @JsonProperty("error_message")
    private String errorMessage;

}
