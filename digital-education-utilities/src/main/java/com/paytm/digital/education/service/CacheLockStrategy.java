package com.paytm.digital.education.service;

import com.paytm.digital.education.method.CachedMethod;
import com.paytm.digital.education.service.impl.CheckData;
import com.paytm.digital.education.service.impl.GetData;
import com.paytm.digital.education.service.impl.Response;
import com.paytm.digital.education.service.impl.WriteData;

public interface CacheLockStrategy {
    <T, U> Response<T, U> getCacheValue(
            String key, GetData<T> getData, CheckData<T> checkData,
            WriteData<U> writeData, CachedMethod<U> cachedMethod);
}
