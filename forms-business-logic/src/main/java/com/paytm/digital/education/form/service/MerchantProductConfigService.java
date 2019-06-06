package com.paytm.digital.education.form.service;

import com.paytm.digital.education.form.model.MerchantProductConfig;

import java.util.List;
import java.util.Set;

public interface MerchantProductConfigService {

    MerchantProductConfig getConfig(String merchantId, String productId, List<String> keys);

    boolean saveConfig(MerchantProductConfig merchantProductConfig);

    List<MerchantProductConfig> getAllConfigs(String paytmMid, Set<String> pids,
            List<String> objects);
}
