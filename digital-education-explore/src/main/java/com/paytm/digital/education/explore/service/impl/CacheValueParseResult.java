package com.paytm.digital.education.explore.service.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;

@RequiredArgsConstructor
@Getter
public class CacheValueParseResult {
    private final DateTime expiryDateTime;
    private final String data;
}
