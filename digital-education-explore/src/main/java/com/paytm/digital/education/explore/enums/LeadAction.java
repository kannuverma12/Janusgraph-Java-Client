package com.paytm.digital.education.explore.enums;

public enum LeadAction {
    GetUpdate, Follow, StopUpdate, Unfollow;

    public static int getCareers360RequestType(LeadAction action) {
        switch (action) {
            case GetUpdate:
                return 1;
            case Follow:
                return 2;
            default:
                return 0;
        }
    }

}

