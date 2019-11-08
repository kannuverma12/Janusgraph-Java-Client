package com.paytm.digital.education.explore.service;

import org.aspectj.lang.ProceedingJoinPoint;

public interface RedisOrchestrator {
    Object get(String key, ProceedingJoinPoint pjp);
}
