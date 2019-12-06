package com.paytm.digital.education.service;

import com.paytm.digital.education.method.CachedMethod;

public interface CacheLockStrategy {
    Object getCacheValue(String key, CachedMethod cachedMethod, boolean shouldCacheNull);
}
