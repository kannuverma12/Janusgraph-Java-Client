package com.paytm.digital.education.utility;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.web.util.UriComponentsBuilder;

import java.beans.PropertyDescriptor;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.paytm.digital.education.ingestion.constant.IngestionConstants.NO;
import static com.paytm.digital.education.ingestion.constant.IngestionConstants.YES;
import static java.util.Map.Entry.comparingByKey;

@UtilityClass
public class CommonUtils {

    private final        String javaLangPackagesStartPath = "java.lang";
    private static final String ASSET_CDN_PREFIX          =
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

    public String convertNameToUrlDisplayName(String name) {
        return name.replaceAll("[^a-zA-Z0-9]+", "-").toLowerCase();
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

    public boolean isDateAfter(Date d1, Date d2) {
        LocalDate jodaDate1 = LocalDate.fromDateFields(d1);
        LocalDate jodaDate2 = LocalDate.fromDateFields(d2);
        return jodaDate1.isAfter(jodaDate2);
    }

    public boolean isDateEqual(Date d1, Date d2) {
        LocalDate jodaDate1 = LocalDate.fromDateFields(d1);
        LocalDate jodaDate2 = LocalDate.fromDateFields(d2);
        return jodaDate1.isEqual(jodaDate2);
    }

    public <T> Predicate<T> distinctBy(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new HashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public static boolean stringToBoolean(final String input) {
        return StringUtils.isNotBlank(input) && YES.equalsIgnoreCase(input.trim());
    }

    public static String booleanToString(final Boolean input) {
        return (Objects.nonNull(input) && input) ? YES : NO;
    }

    public static String toString(Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    public static Long randomLong() {
        long min = 100000000000L;
        long max = 999999999999L;
        return randomLong(min, max);
    }

    public static Long randomLong(Long min, Long max) {
        return min + (long) (Math.random() * (max - min));
    }

    public static Date setLastDateOfMonth(Date inDate) {
        if (Objects.nonNull(inDate)) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(inDate);
            cal.add(Calendar.MONTH, 1);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.add(Calendar.DAY_OF_MONTH, -1);
            return cal.getTime();
        }
        return null;
    }

    public static void copyNonNullProperties(Object src, Object target) {
        BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
    }

    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public static <K extends Comparable<K>, V> LinkedHashMap<K, V> sortMapByKeys(Map<K, V> map) {
        return map.entrySet().stream().sorted(comparingByKey())
                .collect(LinkedHashMap::new, (m, v) -> m.put(v.getKey(), v.getValue()),
                        LinkedHashMap::putAll);
    }
}
