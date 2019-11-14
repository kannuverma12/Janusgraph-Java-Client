package com.paytm.digital.education.service.impl;

import com.paytm.digital.education.exception.OldCacheValueExpiredException;
import com.paytm.digital.education.exception.OldCacheValueNullException;

public interface GetData<T> {
    T doGetData() throws OldCacheValueExpiredException, OldCacheValueNullException;
}
