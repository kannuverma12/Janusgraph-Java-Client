package com.paytm.digital.education.explore.service;

import java.util.List;

public interface CommonMongoService {
    List<String> getFieldsByGroupAndCollectioName(String collectionName, String fieldGroup);
}
