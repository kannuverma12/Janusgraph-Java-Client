package com.paytm.digital.education.service.impl;

import org.joda.time.DateTime;

public interface CacheData {
    DateTime getExpiryDateTime();

    String getData();
}
