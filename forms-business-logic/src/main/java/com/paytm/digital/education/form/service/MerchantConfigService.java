package com.paytm.digital.education.form.service;

import com.paytm.digital.education.form.model.MerchantConfiguration;

import java.util.ArrayList;

public interface MerchantConfigService {
    public MerchantConfiguration getMerchantById(String merchantId, ArrayList<String> keys);

    public void saveOrUpdateMerchantConfiguration(MerchantConfiguration merchantConfiguration);
}
