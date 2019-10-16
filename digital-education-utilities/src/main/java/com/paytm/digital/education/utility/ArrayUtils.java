package com.paytm.digital.education.utility;

public class ArrayUtils {
    public static Object[] padArray(int numberOfArgs, Object[] argsArray) {
        Object[] paddedArray = new Object[numberOfArgs];
        int lower = Math.min(numberOfArgs, argsArray.length);
        int i;
        for (i = 0; i < lower; i++) {
            paddedArray[i] = argsArray[i];
        }
        if (i < numberOfArgs) {
            for (; i < numberOfArgs; i++) {
                paddedArray[i] = "?";
            }
        }
        return paddedArray;
    }
}
