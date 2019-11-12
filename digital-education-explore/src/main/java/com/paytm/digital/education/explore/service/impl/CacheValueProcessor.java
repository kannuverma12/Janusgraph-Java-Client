package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.exception.OldCacheValueExpiredException;
import com.paytm.digital.education.exception.OldCacheValueNullException;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.regex.Pattern;

import static java.lang.Long.parseLong;

@Service
public class CacheValueProcessor {
    private static final String VALUE_DELIMITER = " **##** ";
    private static final Pattern VALUE_DELIMITER_PATTERN = Pattern.compile(" \\*\\*##\\*\\* ");

    public String parseCacheValueAndValidateExpiry(String cacheValue)
            throws OldCacheValueNullException, OldCacheValueExpiredException {
        if (Objects.isNull(cacheValue)) {
            throw new OldCacheValueNullException();
        }
        CacheValueParseResult result = parse(cacheValue);
        if (result.getExpiryDateTime().isAfterNow()) {
            return result.getData();
        }
        throw new OldCacheValueExpiredException();
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
