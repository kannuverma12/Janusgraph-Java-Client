package com.paytm.digital.education.explore.enums;

public enum InstituteStream {
    ENGINEERING(0),
    ENGINEERING_AND_ARCHITECTURE(0),
    COMPUTER_APPLICATIONS_AND_IT(1),
    MEDICINE_AND_ALLIED_SCIENCES(2),
    MEDICAL(2),
    LAW(3),
    MANAGEMENT_AND_BUSINESS_ADMINISTRATION(4),
    MANAGEMENT(4),
    PHARMACY(5),
    MEDIA_MASS_COMMUNICATION_AND_JOURNALISM(6),
    ANIMATION_AND_DESIGN(7),
    SCIENCES(8),
    COMMERCE(9),
    ARTS_HUMANITIES_AND_SOCIAL_SCIENCES(10),
    HOSPITALITY_AND_TOURISM(11),
    EDUCATION(12);

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
