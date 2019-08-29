package com.paytm.digital.education.enums;

public enum Language {
    HINDI("year"),
    ENGLISH("month");

    private String text;

    Language(String text) {
        this.text = text;
    }

    public static Language fromString(String text) {
        for (Language courseType : Language.values()) {
            if (courseType.getText().equalsIgnoreCase(text)) {
                return courseType;
            }
        }
        return null;
    }

    public String getText() {
        return this.text;
    }
}
