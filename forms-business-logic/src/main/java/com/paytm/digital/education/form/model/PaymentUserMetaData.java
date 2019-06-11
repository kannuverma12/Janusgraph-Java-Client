package com.paytm.digital.education.form.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaymentUserMetaData {

    @JsonProperty("reference_id")
    String refId;

    @JsonProperty("transactionId")
    String transactionId;

}
