package com.paytm.digital.education.coaching.utils;

import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ComparisonUtils {

    private static final Logger log = LoggerFactory.getLogger(ComparisonUtils.class);

    private static final double THRESHOLD_DOUBLE = 0.01;
    private static final float  THRESHOLD_FLOAT  = 0.01F;

    public boolean thresholdBasedDoublesComparison(double a, double b) {
        return (Math.abs(a - b) < THRESHOLD_DOUBLE);
    }

    public boolean thresholdBasedFloatsComparison(float a, float b) {
        return (Math.abs(a - b) < THRESHOLD_FLOAT);
    }
}
