package com.paytm.digital.education.enums;

public enum CourseLevel {
    POSTGRADUATE("Postgraduate"),
    UNDERGRADUATE("Undergraduate"),
    DOCTORATE("Doctorate"),
    DIPLOMA("Diploma"),
    SCHOOL("School");

    private String displayName;

    CourseLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public static CourseLevel fromString(String text) {
        for (CourseLevel courseLevel : CourseLevel.values()) {
            if (courseLevel.getDisplayName().equalsIgnoreCase(text)) {
                return courseLevel;
            }
        }
        return null;
    }
}
