package com.paytm.digital.education.explore.enums;

public enum Client {
    WEB("web"), APP("app");
    private final String name;

    Client(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
