package com.paytm.digital.education.enums;

public enum RankingSource {
    NIRF(0),
    CAREERS360(1);

    private int value;

    RankingSource(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
