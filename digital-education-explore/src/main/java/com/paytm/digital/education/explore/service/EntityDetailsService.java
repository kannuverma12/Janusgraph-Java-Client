package com.paytm.digital.education.explore.service;

public interface EntityDetailsService {
    <T> T getEntityDetails(String keyName, long entityId, Class<T> type,
                                  String fieldGroup, String fields);
}
