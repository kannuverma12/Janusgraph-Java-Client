package com.paytm.digital.education.form.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FormIoMerchantResultResponse {

    private FormIoMerchantResponse result;
}
