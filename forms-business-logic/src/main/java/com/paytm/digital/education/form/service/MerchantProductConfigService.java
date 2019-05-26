package com.paytm.digital.education.form.service;

import com.paytm.digital.education.form.model.MerchantProductConfig;

import java.util.ArrayList;

public interface MerchantProductConfigService {

    public MerchantProductConfig getConfig(String merchantId, String productId, ArrayList<String> keys);

    public boolean saveConfig(MerchantProductConfig merchantProductConfig);
}
