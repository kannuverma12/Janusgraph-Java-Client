package com.paytm.digital.education.enums;

public enum PageSection {

    COLLEGES_IN_FOCUS("colleges_focus"),
    LOCATIONS("locations"),
    STREAMS("streams"),
    TOP_COLLEGES("top_colleges"),
    TOP_EXAMS("top_exams"),
    TOP_EXAMS_APP("top_exams_app"),
    BROWSE_BY_EXAM_LEVEL("browse_by_exam_level"),
    TOP_SCHOOLS("top_schools"),
    BANNER_MID("banner_mid");

    private String value;

    PageSection(String val) {
        this.value = val;
    }

    public static PageSection fromValue(String value) {
        for (PageSection section : PageSection.values()) {
            if (section.getValue().equalsIgnoreCase(value)) {
                return section;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }
}
