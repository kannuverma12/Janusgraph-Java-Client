package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.exception.CachedMethodInvocationException;
import com.paytm.digital.education.explore.service.CachedMethod;
import org.aspectj.lang.ProceedingJoinPoint;

public class MethodEnclosedInProceedingJoinPoint implements CachedMethod {
    private final ProceedingJoinPoint proceedingJoinPoint;

    public MethodEnclosedInProceedingJoinPoint(ProceedingJoinPoint proceedingJoinPoint) {
        this.proceedingJoinPoint = proceedingJoinPoint;
    }

    @Override
    public Object invoke() throws CachedMethodInvocationException {
        try {
            Object o = proceedingJoinPoint.proceed();
            return o;
        } catch (Throwable t) {
            throw new CachedMethodInvocationException(t);
        }
    }
}
