package com.paytm.digital.education.coaching.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MerchantNotifyFailureReason {

    MERCHANT_INFRA_DOWN("merchant infra down");

    private String text;

    MerchantNotifyFailureReason(String text) {
        this.text = text;
    }

    public static MerchantNotifyFailureReason fromString(String text) {
        for (MerchantNotifyFailureReason reason : MerchantNotifyFailureReason.values()) {
            if (reason.getText().equalsIgnoreCase(text)) {
                return reason;
            }
        }
        return null;
    }

    @JsonValue
    public String getText() {
        return this.text;
    }
}
