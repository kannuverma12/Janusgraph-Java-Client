package com.paytm.digital.education.form.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FormIoMerchantResponse {

    private String paymentStatus;

    private String merchantTransactionId;

    private Map<String, Object> candidateDetails;

    public void setPaymentStatusToLowerCase() {
        if (this.paymentStatus != null) {
            this.paymentStatus = this.paymentStatus.toLowerCase();
        }
    }
}
