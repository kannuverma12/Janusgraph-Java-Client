package com.paytm.digital.education.method;

import com.paytm.digital.education.exception.CachedMethodInvocationException;
import org.aspectj.lang.ProceedingJoinPoint;

public class MethodEnclosedInProceedingJoinPoint implements CachedMethod<Object> {
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
