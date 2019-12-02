package com.paytm.digital.education.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    private final String readableValue;

    ClassType(String readableValue) {
        this.readableValue = readableValue;
    }

    @JsonValue
    public String getReadableValue() {
        return this.readableValue;
    }

}
