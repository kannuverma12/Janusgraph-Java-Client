package com.paytm.digital.education.explore.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Career360EntityType {
    @JsonProperty("exam")
    EXAM,

    @JsonProperty("course")
    COURSE;

    public static EducationEntity convertToEducationEntity(Career360EntityType educationEntity) {
        switch (educationEntity) {
            case EXAM:
                return EducationEntity.EXAM;
            case COURSE:
                return EducationEntity.COURSE;
            default:
                return null;
        }
    }
}
