package com.paytm.digital.education.coaching.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum MerchantNotifyStatus {

    SUCCESS("SUCCESS"),
    FAILURE("FAILURE"),
    PENDING("PENDING");

    private String text;

    MerchantNotifyStatus(String text) {
        this.text = text;
    }

    public static MerchantNotifyStatus fromString(String text) {
        for (MerchantNotifyStatus status : MerchantNotifyStatus.values()) {
            if (status.getText().equalsIgnoreCase(text)) {
                return status;
            }
        }
        log.error("Could not find MerchantNotifyStatus for text: {}", text);
        return null;
    }

    @JsonValue
    public String getText() {
        return this.text;
    }
}
