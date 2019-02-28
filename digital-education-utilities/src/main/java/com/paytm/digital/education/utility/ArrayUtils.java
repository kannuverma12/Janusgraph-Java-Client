package com.paytm.digital.education.utility;

public class ArrayUtils {
    public static Object[] padArray(int numberOfArgs, Object[] argsArray) {
        Object[] paddedArray = new Object[numberOfArgs];
        for (int i = 0; i < numberOfArgs; i++) {
            if (i < argsArray.length) {
                paddedArray[i] = argsArray[i];
            } else {
                paddedArray[i] = "?";
            }
        }
        return paddedArray;
    }
}
