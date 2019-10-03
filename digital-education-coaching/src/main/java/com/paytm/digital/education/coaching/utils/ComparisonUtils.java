package com.paytm.digital.education.coaching.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class ComparisonUtils {

    private static final double THRESHOLD_DOUBLE = 0.01;
    private static final float  THRESHOLD_FLOAT  = 0.01F;

    public boolean thresholdBasedDoublesComparison(double a, double b) {
        return (Math.abs(a - b) < THRESHOLD_DOUBLE);
    }

    public boolean thresholdBasedFloatsComparison(float a, float b) {
        return (Math.abs(a - b) < THRESHOLD_FLOAT);
    }
}
