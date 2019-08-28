package com.paytm.digital.education.enums;

public enum CourseLevel {
    POSTGRADUATE("Postgraduate"),
    UNDERGRADUATE("Undergraduate"),
    DOCTORATE("Doctorate"),
    DIPLOMA("Diploma");

    private String displayName;

    CourseLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}
