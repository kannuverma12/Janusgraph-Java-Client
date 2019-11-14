package com.paytm.digital.education.service.impl;

import com.paytm.digital.education.exception.CachedMethodInvocationException;

public interface ComputeAndWrite<T> {
    T doComputeAndWrite() throws CachedMethodInvocationException;
}
