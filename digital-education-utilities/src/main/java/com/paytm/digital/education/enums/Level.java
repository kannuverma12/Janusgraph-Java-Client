package com.paytm.digital.education.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

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
        return null;
    }

    @JsonValue
    public String getText() {
        return this.text;
    }
}
