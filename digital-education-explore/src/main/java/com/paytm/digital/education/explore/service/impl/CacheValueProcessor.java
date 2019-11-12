package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.exception.OldCacheValueExpiredException;
import com.paytm.digital.education.exception.OldCacheValueNullException;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

@Service
public class CacheValueProcessor {
    private final String VALUE_DELIMITER = " \\*\\*##\\*\\* ";

    public String fromCacheValueFormatIfValid(String cacheValue) throws OldCacheValueNullException, OldCacheValueExpiredException {
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
        return ttlMillis + VALUE_DELIMITER + value;
    }

    public CacheValueParseResult parse(String cacheValue) {
        if (cacheValue == null) return null;
        String[] parts = cacheValue.split(VALUE_DELIMITER, 1);
        DateTime expiryDateTime = new DateTime(parts[0]);
        return new CacheValueParseResult(expiryDateTime, parts[1]);
    }
}
