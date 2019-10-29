package com.paytm.digital.education.enums;

public enum CourseCover {
    FULL_SYLLABUS("Full Syllabus"),
    ALL_INDIA("ALL India"),
    SUBJECT_WISE("Subject wise"),
    MOCK_PRACTICE("Mock Practice");

    private String text;

    CourseCover(String text) {
        this.text = text;
    }

    public static CourseCover fromString(String text) {
        for (CourseCover courseType : CourseCover.values()) {
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
