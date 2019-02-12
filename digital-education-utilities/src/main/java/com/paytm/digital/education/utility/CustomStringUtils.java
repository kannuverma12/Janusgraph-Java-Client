package com.paytm.digital.education.utility;


import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.text.CaseUtils.toCamelCase;

public class CustomStringUtils {

    public static String convertStreamNameToDisplayName(String name) {
        return stream(name.split("_"))
            .map(s -> toCamelCase(s, true)).collect(joining(" "));
    }
}
