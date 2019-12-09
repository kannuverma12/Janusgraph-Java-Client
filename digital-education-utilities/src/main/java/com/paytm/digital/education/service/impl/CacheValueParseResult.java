package com.paytm.digital.education.service.impl;

import com.paytm.digital.education.service.CacheData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;

@RequiredArgsConstructor
@Getter
public class CacheValueParseResult implements CacheData {
    private final DateTime expiryDateTime;
    private final String data;
}

