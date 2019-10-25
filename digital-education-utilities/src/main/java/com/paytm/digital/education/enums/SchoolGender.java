package com.paytm.digital.education.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SchoolGender {
    GIRLS("Girls"),
    BOYS("Boys"),
    CO_ED("Co-ed"),
    NOT_PROVIDED("");

    private final String readableValue;

    SchoolGender(String readableValue) {
        this.readableValue = readableValue;
    }

    @JsonValue
    public String getReadableValue() {
        return this.readableValue;
    }
}
