package com.paytm.digital.education.service;

import com.paytm.digital.education.method.CachedMethod;

public interface RedisOrchestrator {
    <U> Object get(String key, CachedMethod<U> cachedMethod);
}

