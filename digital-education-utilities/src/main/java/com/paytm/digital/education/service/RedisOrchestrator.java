package com.paytm.digital.education.service;

import com.paytm.digital.education.method.CachedMethod;

public interface RedisOrchestrator {
    Object get(String key, CachedMethod cachedMethod, boolean shouldCacheNull);
}

