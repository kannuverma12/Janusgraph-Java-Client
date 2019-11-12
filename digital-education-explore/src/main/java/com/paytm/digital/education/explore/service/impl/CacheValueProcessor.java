package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.exception.OldCacheValueExpiredException;
import com.paytm.digital.education.exception.OldCacheValueNullException;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

import static java.lang.Long.parseLong;

@Service
public class CacheValueProcessor {
    private static final String VALUE_DELIMITER = " **##** ";
    private static final Pattern VALUE_DELIMITER_PATTERN = Pattern.compile(" \\*\\*##\\*\\* ");

    public String fromCacheValueFormatIfValid(String cacheValue)
            throws OldCacheValueNullException, OldCacheValueExpiredException {
        if (cacheValue == null) {
            throw new OldCacheValueNullException();
        }
        CacheValueParseResult result = parse(cacheValue);
        if (result.getExpiryDateTime().isAfterNow()) {
            return result.getData();
        }
        throw new OldCacheValueExpiredException();
    }

    public String toCacheValueFormat(String value, long ttlMillis) {
        return new DateTime().plusMillis((int) ttlMillis).getMillis() + VALUE_DELIMITER + value;
    }

    public CacheValueParseResult parse(String cacheValue) {
        if (cacheValue == null) {
            return null;
        }
        String[] parts = VALUE_DELIMITER_PATTERN.split(cacheValue, 2);
        DateTime expiryDateTime = new DateTime(parseLong(parts[0]));
        return new CacheValueParseResult(expiryDateTime, parts[1]);
    }
}
