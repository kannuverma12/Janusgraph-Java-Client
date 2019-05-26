package com.paytm.digital.education.form.response;

import lombok.Data;

@Data
public class FormIoMerchantResponse {

    private String paymentStatus;

    private String merchantTransactionId;
}
