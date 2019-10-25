package com.paytm.digital.education.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum StudentCategory {
    GENERAL("General"),
    OBC("obc"),
    SC("sc"),
    ST("st"),
    EWS("ews");

    private String text;

    StudentCategory(String text) {
        this.text = text;
    }

    @JsonValue
    public String getText() {
        return this.text;
    }

    public static StudentCategory fromString(String text) {
        for (StudentCategory examType : StudentCategory.values()) {
            if (examType.getText().equalsIgnoreCase(text)) {
                return examType;
            }
        }
        return null;
    }
}
