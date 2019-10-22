package com.paytm.digital.education.explore.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ClassLevel {
    @JsonProperty("Kindergarten")
    KINDERGARTEN,

    @JsonProperty("Nursery")
    NURSERY,

    @JsonProperty("Primary School")
    PRIMARY_SCHOOL,

    @JsonProperty("Middle School")
    MIDDLE_SCHOOL,

    @JsonProperty("Secondary School")
    SECONDARY_SCHOOL,

    @JsonProperty("Senior Secondary School")
    SENIOR_SECONDARY_SCHOOL
}
