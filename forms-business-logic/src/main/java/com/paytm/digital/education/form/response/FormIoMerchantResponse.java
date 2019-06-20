package com.paytm.digital.education.form.response;

import lombok.Data;

import java.util.Map;

@Data
public class FormIoMerchantResponse {

    private String paymentStatus;

    private String merchantTransactionId;

    private Map<String, Object> candidateDetails;
}
