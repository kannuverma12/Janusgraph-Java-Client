package com.paytm.digital.education.explore.service;

public interface RedisOrchestrator {
    Object get(String key, CachedMethod cachedMethod);
}
