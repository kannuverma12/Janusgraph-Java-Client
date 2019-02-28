package com.paytm.digital.education.explore.service;

import java.util.List;

public interface EntityDetailsService {
    <T> T getEntityDetails(String keyName, long entityId, Class<T> type,
                                  String fieldGroup, List<String> fields);
}
