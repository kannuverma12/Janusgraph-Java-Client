package com.paytm.digital.education.coaching.enums;

public enum ExamSectionType {

    ELIGIBILITY("eligibility"),

    SYLLABUS("syllabus"),

    COUNSELLING("documents_counselling"),

    CUTOFF("cutoff"),

    RESULT("result"),

    ADMIT_CARD("admit_card"),

    EXAM_PATTERN("exam_pattern"),

    APPLICATION_FORM("application_form");

    private String key;

    ExamSectionType(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

}
