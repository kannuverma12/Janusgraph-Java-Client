package com.paytm.digital.education.form.service;

import java.io.IOException;
import java.util.Map;

import com.paytm.digital.education.form.model.FormData;

public interface DownloadService {

    byte[] getTempAimaResponse(Long orderId, Map<String, Object> url,String customerId);

    FormData getFormDataByUserIdAndOrderId(String userId, Long orderId);

    FormData getFormDataByMerchantIdAndOrderId(String merchantId, Long orderId);

    byte[] getPdfByteArray(FormData model, String type) throws IOException;

    FormData getFormDataByOrderId(Long orderId);

}
