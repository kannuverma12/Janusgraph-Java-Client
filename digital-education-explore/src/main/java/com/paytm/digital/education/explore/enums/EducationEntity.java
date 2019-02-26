package com.paytm.digital.education.explore.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

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
    CITY;
}
