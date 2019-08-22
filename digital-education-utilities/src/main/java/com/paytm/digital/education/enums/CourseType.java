package com.paytm.digital.education.enums;

public enum CourseType {
    CLASSROOM_PROGRAM("Classroom program"),
    DISTANCE_LEARNING("Distance learning"),
    TEST_SERIES_ONLINE("Test Series : Online"),
    TEST_SERIES_OFFLINE("Test Series : Offline"),
    E_LEARNING("E Learning");

    private String text;

    CourseType(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public static CourseType fromString(String text) {
        for (CourseType courseType : CourseType.values()) {
            if (courseType.getText().equalsIgnoreCase(text)) {
                return courseType;
            }
        }
        return null;
    }
}
