package com.paytm.digital.education.explore.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ClassLevel {
    @JsonProperty("Kindergarten")
    KINDERGARTEN,

    @JsonProperty("Nursery")
    NURSERY,

    @JsonProperty("Primary")
    PRIMARY_SCHOOL,

    @JsonProperty("Middle")
    MIDDLE_SCHOOL,

    @JsonProperty("Secondary")
    SECONDARY_SCHOOL,

    @JsonProperty("Senior Secondary")
    SENIOR_SECONDARY_SCHOOL
}
