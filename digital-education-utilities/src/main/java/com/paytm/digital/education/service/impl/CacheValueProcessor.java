package com.paytm.digital.education.service.impl;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.regex.Pattern;

import static java.lang.Long.parseLong;

@Service
public class CacheValueProcessor {
    private static final String VALUE_DELIMITER = " **##** ";
    private static final Pattern VALUE_DELIMITER_PATTERN = Pattern.compile(" \\*\\*##\\*\\* ");

    public CacheValueParseResult parseCacheValue(String cacheValue) {
        if (Objects.isNull(cacheValue)) {
            return new CacheValueParseResult(null, null);
        }
        return parse(cacheValue);
    }

    public String appendExpiryDateToValue(String value, int ttlMillis) {
        return new DateTime().plusMillis(ttlMillis).getMillis() + VALUE_DELIMITER + value;
    }

    public CacheValueParseResult parse(String cacheValue) {
        if (Objects.isNull(cacheValue)) {
            return null;
        }
        String[] parts = VALUE_DELIMITER_PATTERN.split(cacheValue, 2);
        DateTime expiryDateTime = new DateTime(parseLong(parts[0]));
        return new CacheValueParseResult(expiryDateTime, parts[1]);
    }
}

