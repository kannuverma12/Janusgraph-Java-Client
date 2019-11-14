package com.paytm.digital.education.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
public enum EducationEntity {

    @JsonProperty("exam")
    EXAM,

    @JsonProperty("institute")
    INSTITUTE,

    @JsonProperty("school")
    SCHOOL,

    @JsonProperty("course")
    COURSE,

    @JsonProperty("state")
    STATE,

    @JsonProperty("city")
    CITY,

    @JsonProperty("stream")
    STREAM,

    @JsonProperty("location")
    LOCATION,

    @JsonProperty("recent_searches")
    RECENT_SEARCHES,

    @JsonProperty("coaching_institute")
    COACHING_INSTITUTE,

    @JsonProperty("coaching_course")
    COACHING_COURSE,

    @JsonProperty("coaching_center")
    COACHING_CENTER;

    public static Career360EntityType convertToCareer360entity(EducationEntity educationEntity) {
        switch (educationEntity) {
            case EXAM:
                return Career360EntityType.exam;
            case COURSE:
                return Career360EntityType.course;
            default:
                return null;
        }
    }

    public static SubscribableEntityType convertToSubscribableEntity(
            EducationEntity educationEntity) {
        switch (educationEntity) {
            case INSTITUTE:
                return SubscribableEntityType.INSTITUTE;
            case EXAM:
                return SubscribableEntityType.EXAM;
            case COURSE:
                return SubscribableEntityType.COURSE;
            case SCHOOL:
                return SubscribableEntityType.SCHOOL;
            default:
                return null;
        }
    }

    public static EducationEntity getEntityFromValue(String value) {
        for (EducationEntity educationEntity : EducationEntity.values()) {
            if (educationEntity.name().equalsIgnoreCase(value)) {
                return educationEntity;
            }
        }
        return null;
    }
}
