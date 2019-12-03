package com.paytm.digital.education.service.impl;

import com.paytm.digital.education.method.CachedMethod;
import com.paytm.digital.education.service.CacheLockStrategy;
import com.paytm.digital.education.service.RedisOrchestrator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class RedisOrchestratorImpl implements RedisOrchestrator {
    private final CacheLockStrategy cacheLockStrategy;

    public RedisOrchestratorImpl(
            @Qualifier("writeLockStrategy") CacheLockStrategy cacheLockStrategy) {
        this.cacheLockStrategy = cacheLockStrategy;
    }

    @Override
    public Object get(String key, CachedMethod cachedMethod) {
        return cacheLockStrategy.getCacheValue(key, cachedMethod);
    }
}
