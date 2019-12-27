package com.paytm.digital.education.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ClassType {
    PRE_NURSERY("Pre-nursery"),
    NURSERY("Nursery"),
    LKG("Lower Kindergarten (LKG)", "LKG"),
    UKG("Upper Kindergarten (UKG)", "UKG"),
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

    @JsonProperty("short_name")
    private final String shortName;

    @JsonProperty("long_name")
    private final String longName;

    ClassType(String name) {
        this(name, name);
    }

    ClassType(String longName, String shortName) {
        this.longName = longName;
        this.shortName = shortName;
    }
}
