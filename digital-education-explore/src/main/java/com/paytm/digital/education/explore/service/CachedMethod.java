package com.paytm.digital.education.explore.service;


import com.paytm.digital.education.exception.CachedMethodInvocationException;

public interface CachedMethod {
    Class getReturnType();

    Object invoke() throws CachedMethodInvocationException;
}
