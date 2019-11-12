package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.exception.CachedMethodInvocationException;
import com.paytm.digital.education.explore.service.CachedMethod;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

public class MethodEnclosedInProceedingJoinPoint implements CachedMethod {
    private final Class returnType;
    private final ProceedingJoinPoint proceedingJoinPoint;

    public MethodEnclosedInProceedingJoinPoint(ProceedingJoinPoint proceedingJoinPoint) {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        this.returnType = methodSignature.getReturnType();
        this.proceedingJoinPoint = proceedingJoinPoint;
    }

    @Override
    public Class getReturnType() {
        return returnType;
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
