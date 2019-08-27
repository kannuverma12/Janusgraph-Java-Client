package com.paytm.digital.education.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ExamType {
    TEST_SERIES("Test Series"),
    PERIODIC_TEST("Periodic Test"),
    ENTRANCE("Entrance Exam"),
    SCHOLARSHIP("Scholarship");

    private String text;

    ExamType(String text) {
        this.text = text;
    }

    @JsonValue
    public String getText() {
        return this.text;
    }

    public static ExamType fromString(String text) {
        for (ExamType examType : ExamType.values()) {
            if (examType.getText().equalsIgnoreCase(text)) {
                return examType;
            }
        }
        return null;
    }
}
