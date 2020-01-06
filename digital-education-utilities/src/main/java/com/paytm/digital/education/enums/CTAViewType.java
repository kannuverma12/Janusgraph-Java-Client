package com.paytm.digital.education.enums;

public enum CTAViewType {

    DETAILS("details"),
    LIST("list"),
    POST_ORDER("post_order");

    private String text;

    CTAViewType(String text) {
        this.text = text;
    }

    public static CTAViewType fromString(String text) {
        for (CTAViewType ctaViewType : CTAViewType.values()) {
            if (ctaViewType.getText().equalsIgnoreCase(text)) {
                return ctaViewType;
            }
        }
        return null;
    }

    public String getText() {
        return this.text;
    }

}
