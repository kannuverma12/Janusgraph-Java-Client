package com.paytm.digital.education.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SchoolOwnershipType {
    STATE("State Government"),
    LOCAL_GOVT_BODY("Local Government Body"),
    CENTRAL_GOVERNMENT("Central Government"),
    UNAIDED("Unaided/Independent"),
    GOVERNMENT("Government"),
    AIDED("Aided"),
    PRIVATE("Private"),
    NOT_PROVIDED("");

    private final String readableValue;

    SchoolOwnershipType(String readableValue) {
        this.readableValue = readableValue;
    }

    @JsonValue
    public String getReadableValue() {
        return this.readableValue;
    }
}
