package com.paytm.digital.education.coaching.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.paytm.digital.education.coaching.exeptions.InvalidRequestException;

public enum Level {
    SCHOOL("School"),
    UNDERGRADUATE("Under Graduate"),
    POSTGRADUATE("Post Graduate");

    private String text;

    Level(String text) {
        this.text = text;
    }

    @JsonCreator
    public static Level fromString(String text) {
        for (Level level : Level.values()) {
            if (level.getText().equalsIgnoreCase(text)) {
                return level;
            }
        }
        throw new InvalidRequestException("Unknown level requested : " + text);
    }

    @JsonValue
    public String getText() {
        return this.text;
    }
}
