package com.paytm.digital.education.explore.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@ToString
public enum EducationEntity {

    @JsonProperty("exam")
    EXAM,

    @JsonProperty("institute")
    INSTITUTE,

    @JsonProperty("course")
    COURSE,

    @JsonProperty("state")
    STATE,

    @JsonProperty("city")
    CITY,
    
    @JsonProperty("stream")
    STREAM;

    public static SubscribableEntityType convertToSubscribableEntity(
            EducationEntity educationEntity) {
        switch (educationEntity) {
            case INSTITUTE:
                return SubscribableEntityType.INSTITUTE;
            case EXAM:
                return SubscribableEntityType.EXAM;
            case COURSE:
                return SubscribableEntityType.COURSE;
            default:
                return null;
        }
    }
}
