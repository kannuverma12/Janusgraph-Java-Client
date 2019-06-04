package com.paytm.digital.education.explore.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum CourseStream {
    @JsonProperty("engineering")
    ENGINEERING(0),
    @JsonProperty("engineering_and_architecture")
    ENGINEERING_AND_ARCHITECTURE(0),
    @JsonProperty("computer_application_and_it")
    COMPUTER_APPLICATIONS_AND_IT(1),
    @JsonProperty("medicine_and_allied_sciences")
    MEDICINE_AND_ALLIED_SCIENCES(2),
    @JsonProperty("medical")
    MEDICAL(2),
    @JsonProperty("law")
    LAW(3),
    @JsonProperty("management_and_business_administration")
    MANAGEMENT_AND_BUSINESS_ADMINISTRATION(4),
    @JsonProperty("management")
    MANAGEMENT(4),
    @JsonProperty("pharmacy")
    PHARMACY(5),
    @JsonProperty("media_mass_communication_and_journalism")
    MEDIA_MASS_COMMUNICATION_AND_JOURNALISM(6),
    @JsonProperty("animation_and_design")
    ANIMATION_AND_DESIGN(7),
    @JsonProperty("sciences")
    SCIENCES(8),
    @JsonProperty("commerce")
    COMMERCE(9),
    @JsonProperty("arts_humanities_and_social_sciences")
    ARTS_HUMANITIES_AND_SOCIAL_SCIENCES(10),
    @JsonProperty("hospitality_and_tourism")
    HOSPITALITY_AND_TOURISM(11),
    @JsonProperty("education")
    EDUCATION(12);

    private String key;
    private int    value;

    CourseStream(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public CourseStream convert(String data) {
        for (CourseStream instituteStream : CourseStream.values()) {
            if (instituteStream.name().equalsIgnoreCase(data)) {
                return instituteStream;
            }
        }
        return null;
    }
}
