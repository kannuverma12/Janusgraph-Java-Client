package com.paytm.digital.education.utility;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.helpers.MessageFormatter;

@UtilityClass
public class CommonUtils {

    private final String javaLangPackagesStartPath = "java.lang";

    public String messageFormat(String msg, Object... objs) {
        return MessageFormatter.arrayFormat(msg, objs).getMessage();
    }

    public static boolean isLangSpecific(Class<?> cls) {
        return (cls.isPrimitive() || cls.getName().startsWith(javaLangPackagesStartPath)) ? true
                : false;
    }

    public static String extractValueOfSubstringKey(String originalStr, String key,
            String nextSeparator) {
        String subStr = null;

        if (StringUtils.isBlank(originalStr) || StringUtils.isBlank(key)
                || StringUtils.isBlank(nextSeparator)) {
            return subStr;
        }

        int indexOfKey = originalStr.indexOf(key);
        int indexOfKeyAfterSeparator = originalStr.indexOf(nextSeparator, indexOfKey);
        if (indexOfKeyAfterSeparator > originalStr.length()) {
            return null;
        }

        subStr = originalStr.substring(indexOfKey + key.length(), indexOfKeyAfterSeparator);

        return subStr;

    }

    public String convertNameToUrlDisplayName(String name) {
        return name.replaceAll("[^a-zA-Z0-9]+", "-").toLowerCase();
    }
}
