package com.paytm.digital.education.utility;

import java.util.Collection;

public class Utility {

    public static boolean nullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static boolean nonEmpty(String value) {
        return !nullOrEmpty(value);
    }

    public static <T> boolean nullOrEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    public static <T> boolean nonEmpty(T[] array) {
        return !nullOrEmpty(array);
    }

    public static <T> boolean nullOrEmpty(Collection<T> list) {
        return list == null || list.size() == 0;
    }

    public static <T> boolean nonEmpty(Collection<T> list) {
        return !nullOrEmpty(list);
    }

    public static boolean nullOrEmpty(byte[] array) {
        return array == null || array.length == 0;
    }

    public static boolean nonEmpty(byte[] array) {
        return !nullOrEmpty(array);
    }

}
