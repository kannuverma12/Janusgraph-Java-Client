package com.paytm.digital.education.enums;

public enum Language {
    HINDI("Hindi"),
    ENGLISH("English"),
    BENGALI("Bengali"),
    TELUGU("Telugu"),
    MARATHI("Marathi"),
    TAMIL("Tamil"),
    URDU("Urdu"),
    KANNADA("Kannada"),
    GUJRATI("Gujrati"),
    ODIA("Odia"),
    MALAYALAM("Malayalam"),
    PUNJABI("Punjabi");

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
