package com.paytm.digital.education.form.service;

import com.paytm.digital.education.form.request.PaymentPostingRequest;

public interface PaymentPostingService {
    public boolean processPaymentPosting(PaymentPostingRequest paymentPostingRequest);
}
