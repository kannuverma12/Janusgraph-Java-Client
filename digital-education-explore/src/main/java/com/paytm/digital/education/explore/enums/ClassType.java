package com.paytm.digital.education.explore.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ClassType {
    PRE_NURSERY("Pre Nursery"),
    NURSERY("Nursery"),
    LKG("Lower Kinder Garten (LKG)"),
    UKG("Upper Kinder Garten (UKG)"),
    ONE("1st"),
    TWO("2nd"),
    THREE("3rd"),
    FOUR("4th"),
    FIVE("5th"),
    SIX("6th"),
    SEVEN("7th"),
    EIGHT("8th"),
    NINE("9th"),
    TEN("10th"),
    ELEVEN("11th"),
    TWELVE("12th"),
    NOT_PROVIDED("");

    private final String name;

    private ClassType(String name) {
        this.name = name;
    }

    @JsonValue
    final String value() {
        return this.name;
    }
}
