package com.paytm.digital.education.explore.enums;

public enum LeadAction {
    GetUpdate, Follow, StopUpdate, Unfollow;

    public static LeadAction getNextAction(LeadAction action) {
        switch (action) {
            case GetUpdate:
                return StopUpdate;
            case StopUpdate:
                return GetUpdate;
            case Follow:
                return Unfollow;
            case Unfollow:
                return Follow;
            default:
                return null;
        }
    }

}

