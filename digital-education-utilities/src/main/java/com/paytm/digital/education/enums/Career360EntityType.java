package com.paytm.digital.education.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Career360EntityType {
    @JsonProperty("exam")
    exam,

    @JsonProperty("course")
    course;

    public static EducationEntity convertToEducationEntity(Career360EntityType educationEntity) {
        switch (educationEntity) {
            case exam:
                return EducationEntity.EXAM;
            case course:
                return EducationEntity.COURSE;
            default:
                return null;
        }
    }
}
