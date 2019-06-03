package com.paytm.digital.education.form.service;

import com.paytm.digital.education.form.model.MerchantConfiguration;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Map;

public interface MerchantConfigService {
    MerchantConfiguration getMerchantById(String merchantId, ArrayList<String> keys);

    void saveOrUpdateMerchantConfiguration(MerchantConfiguration merchantConfiguration);

    Map<String, Object> getPostScreenData(String merchantId, Long orderId);

    ResponseEntity<Object> getResponseForPostOrderScreenConfig(
            Map<String, Object> data, Long orderId, String merchantId);
}
