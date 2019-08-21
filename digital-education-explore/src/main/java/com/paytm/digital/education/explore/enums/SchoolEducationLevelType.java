package com.paytm.digital.education.explore.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SchoolEducationLevelType {
    PRIMARY("Primary School"),
    MIDDLE("Upper Primary/Middle School"),
    SECONDARY("Secondary School"),
    SENIOR_SECONDARY("Sr. Secondary/Higher Secondary School"),
    JUNIOR_COLLEGE("Junior College/Intermediate College");

    private final String name;

    SchoolEducationLevelType(String name) {
        this.name = name;
    }

    @JsonValue
    final String value() {
        return this.name;
    }

}
