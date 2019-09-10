package com.paytm.digital.education.enums;

public enum Language {
    HINDI("hindi"),
    ENGLISH("english"),
    BENGALI("bengali"),
    TELUGU("telugu"),
    MARATHI("marathi"),
    TAMIL("tamil"),
    URDU("urdu"),
    KANNADA("kannada"),
    GUJRATI("gujrati"),
    ODIA("odia"),
    MALAYALAM("malayalam"),
    PUNJABI("punjabi");

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
