package com.paytm.digital.education.service.impl;


import com.paytm.digital.education.exception.OldCacheValueExpiredException;
import com.paytm.digital.education.exception.OldCacheValueNullException;

public interface CheckData<T> {
    void doCheckData(T data) throws OldCacheValueExpiredException, OldCacheValueNullException;
}
