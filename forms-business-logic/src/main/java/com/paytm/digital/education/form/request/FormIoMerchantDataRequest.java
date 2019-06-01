package com.paytm.digital.education.form.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.form.model.CandidateDetails;
import lombok.Data;

import java.util.Map;

@Data
public class FormIoMerchantDataRequest {

    @JsonProperty("orderId")
    String orderId;

    @JsonProperty("itemId")
    String itemId;

    @JsonProperty("refId")
    String refId;

    @JsonProperty("amount")
    String amount;

    @JsonProperty("submit")
    Boolean submit;

    @JsonProperty("candidateDetails")
    CandidateDetails candidateDetails;

    @JsonProperty("additionalData")
    private Map<String, Object> additionalData;

}
