package com.paytm.digital.education.service.impl;

import org.springframework.data.redis.core.RedisOperations;

public interface WriteData<T> {
    <K, V> void doWriteData(RedisOperations<K, V> operations, T data);
}
