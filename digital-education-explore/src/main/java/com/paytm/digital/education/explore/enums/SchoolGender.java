package com.paytm.digital.education.explore.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SchoolGender {
    GIRLS("Girls"),
    BOYS("Boys"),
    CO_ED("Co-ed"),
    NOT_PROVIDED("");

    private final String name;

    SchoolGender(String name) {
        this.name = name;
    }

    @JsonValue final String value() {
        return this.name;
    }
}
