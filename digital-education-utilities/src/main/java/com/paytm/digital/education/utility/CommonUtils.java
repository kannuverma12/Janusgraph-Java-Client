package com.paytm.digital.education.utility;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Date;

@UtilityClass
public class CommonUtils {

    private final String javaLangPackagesStartPath = "java.lang";
    private static final String ASSET_CDN_PREFIX              =
            "https://assetscdn1.paytm.com/educationwebassets/education/explore/school/images";

    public String messageFormat(String msg, Object... objs) {
        return MessageFormatter.arrayFormat(msg, objs).getMessage();
    }

    public boolean isLangSpecific(Class<?> cls) {
        return (cls.isPrimitive() || cls.getName().startsWith(javaLangPackagesStartPath)) ? true
                : false;
    }

    public String extractValueOfSubstringKey(String originalStr, String key,
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

    public boolean isNullOrZero(Integer i) {
        return i == null || i == 0;
    }

    public String encodeUrl(String s) {
        return UriComponentsBuilder.fromUriString(s).toUriString();
    }

    public String addCDNPrefixAndEncode(String s) {
        return encodeUrl(ASSET_CDN_PREFIX + s);
    }

    public boolean isDateEqualsOrAfter(Date d1, Date d2) {
        LocalDate jodaDate1 = LocalDate.fromDateFields(d1);
        LocalDate jodaDate2 = LocalDate.fromDateFields(d2);
        return jodaDate1.equals(jodaDate2) || jodaDate1.isAfter(jodaDate2);
    }
}
