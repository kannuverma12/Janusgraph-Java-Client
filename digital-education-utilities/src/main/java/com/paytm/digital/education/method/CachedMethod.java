package com.paytm.digital.education.method;

import com.paytm.digital.education.exception.CachedMethodInvocationException;

public interface CachedMethod<U> {
    U invoke() throws CachedMethodInvocationException;
}

