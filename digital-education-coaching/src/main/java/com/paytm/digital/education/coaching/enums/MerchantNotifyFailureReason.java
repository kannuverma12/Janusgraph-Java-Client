package com.paytm.digital.education.coaching.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum MerchantNotifyFailureReason {

    MERCHANT_INFRA_DOWN("merchant infra down"),
    BAD_REQUEST_IN_MERCHANT_COMMIT("Bad Request in merchant commit"),
    BAD_RESPONSE_FROM_MERCHANT_COMMIT("Bad Response from merchant commit");

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
        log.error("Could not find MerchantNotifyFailureReason for text: {}", text);
        return null;
    }

    @JsonValue
    public String getText() {
        return this.text;
    }
}
