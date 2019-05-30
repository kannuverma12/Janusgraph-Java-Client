package com.paytm.digital.education.form.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FormIoMerchantRequest {

    @JsonProperty("data")
    FormIoMerchantDataRequest data;

    @JsonProperty("state")
    String state;
}
