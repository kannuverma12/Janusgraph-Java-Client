package com.paytm.digital.education.explore.enums;

public enum ExamSectionType {

    ELIGIBILITY("eligibility"),

    SYLLABUS("syllabus"),

    COUNSELLING("documents_counselling"),

    CUTOFF("cutoff"),

    RESULT("result"),

    ADMIT_CARD("admit_card"),

    EXAM_PATTERN("exam_pattern"),

    APPLICATION_FORM("application_form"),

    TERMS_AND_CONDITIONS("terms_and_conditions"),

    PRIVACY_POLICIES("privacy_policies"),

    DISCLAIMER("disclaimer");

    private String key;

    ExamSectionType(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}
