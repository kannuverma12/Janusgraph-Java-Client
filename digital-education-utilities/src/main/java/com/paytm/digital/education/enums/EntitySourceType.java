package com.paytm.digital.education.enums;

public enum EntitySourceType {
    PAYTM("paytm"), C360("c360");
    private final String name;

    EntitySourceType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
