package com.paytm.digital.education.explore.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SchoolOwnershipType {
    STATE("State Government"),
    LOCAL_GOVT_BODY("Local Government Body"),
    CENTRAL_GOVERNMENT("Central Government"),
    UNAIDED("Unaided/Independent"),
    GOVERNMENT("Government"),
    AIDED("Aided"),
    NOT_PROVIDED("");

    private final String name;

    SchoolOwnershipType(String name) {
        this.name = name;
    }

    @JsonValue
    final String value() {
        return this.name;
    }
}
