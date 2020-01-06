package com.paytm.digital.education.service;

import org.joda.time.DateTime;

public interface CacheData {
    DateTime getExpiryDateTime();

    String getData();
}
