package com.paytm.digital.education.explore.enums;

public enum InstituteStream {
    ENGINEERING(0),
    ENGINEERING_AND_ARCHITECTURE(0),
    MEDICINE_AND_ALLIED_SCIENCES(1),
    MANAGEMENT_AND_BUSINESS_ADMINISTRATION(2),
    LAW(3),
    PHARMACY(4),
    ANIMATION_AND_DESIGN(5);

    private String key;
    private int value;

    InstituteStream(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public InstituteStream convert(String data) {
        for (InstituteStream instituteStream : InstituteStream.values()) {
            if (instituteStream.name().equalsIgnoreCase(data)) {
                return instituteStream;
            }
        }
        return null;
    }
}
