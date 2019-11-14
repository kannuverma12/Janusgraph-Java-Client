package com.paytm.digital.education.method;

import com.paytm.digital.education.exception.CachedMethodInvocationException;

public interface CachedMethod {
    Object invoke() throws CachedMethodInvocationException;
}

