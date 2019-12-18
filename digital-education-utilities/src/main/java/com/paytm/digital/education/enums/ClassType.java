package com.paytm.digital.education.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ClassType {
    PRE_NURSERY("Pre-nursery"),
    NURSERY("Nursery"),
    LKG("Lower Kindergarten (LKG)"),
    UKG("Upper Kindergarten (UKG)"),
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

    private final String readableValue;

    ClassType(String readableValue) {
        this.readableValue = readableValue;
    }

    @JsonValue
    public String getReadableValue() {
        return this.readableValue;
    }

}
