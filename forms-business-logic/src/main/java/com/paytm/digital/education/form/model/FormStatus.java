package com.paytm.digital.education.form.model;

public enum FormStatus {
    REGISTERED, // registed successfully
    PARTIAL, // fill form saved
    PAYMENT_PENDING, // proceed to pay to cart
    PG_PAYMENT_DONE, // payment posting called
    SUCCESS, // call partner and got success
    FAILURE, // call partner and got failure
    PENDING,
    ADDONS
}
