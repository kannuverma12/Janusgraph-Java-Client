package com.paytm.digital.education.enums;

public enum Number {

    ZERO(0),
    ONE(1),
    TWO(2),
    THREE(3);

    private int value;

    Number(int val) {
        this.value = val;
    }

    public int getValue() {
        return this.value;
    }

    public Number convertFromValue(int val) {
        for (Number number : Number.values()) {
            if (val == number.value) {
                return number;
            }
        }
        return null;
    }

}
