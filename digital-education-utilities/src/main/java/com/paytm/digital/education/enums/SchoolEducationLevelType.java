package com.paytm.digital.education.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum SchoolEducationLevelType {
    PRIMARY("Primary School"),
    MIDDLE("Upper Primary/Middle School"),
    SECONDARY("Secondary School"),
    SENIOR_SECONDARY("Sr. Secondary/Higher Secondary School"),
    JUNIOR_COLLEGE("Junior College/Intermediate College");

    private final String readableValue;

    SchoolEducationLevelType(String readableValue) {
        this.readableValue = readableValue;
    }

    @JsonValue
    public String getReadableValue() {
        return this.readableValue;
    }

}
