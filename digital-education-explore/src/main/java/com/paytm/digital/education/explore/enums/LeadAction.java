package com.paytm.digital.education.explore.enums;

public enum LeadAction {
    GetUpdates, GetInTouch;

    public static int getCareers360RequestType(LeadAction action) {
        switch (action) {
            case GetUpdates:
                return 1;
            case GetInTouch:
                return 2;
            default:
                return 0;
        }
    }

}

