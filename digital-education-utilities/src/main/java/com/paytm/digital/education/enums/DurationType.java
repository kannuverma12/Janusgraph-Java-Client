package com.paytm.digital.education.enums;

public enum DurationType {
    YEAR("year"),
    MONTH("month"),
    WEEK("week"),
    DAY("day"),
    HOUR("hour"),
    MINUTE("minute"),
    SECOND("second");

    private String text;

    DurationType(String text) {
        this.text = text;
    }

    public static DurationType fromString(String text) {
        for (DurationType courseType : DurationType.values()) {
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
