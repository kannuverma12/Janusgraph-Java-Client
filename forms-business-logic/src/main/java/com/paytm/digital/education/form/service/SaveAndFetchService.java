package com.paytm.digital.education.form.service;

import com.paytm.digital.education.form.model.FormData;
import com.paytm.digital.education.form.model.FormStatus;

public interface SaveAndFetchService {

    String saveData(FormData formData, boolean confirmFlag);

    String paymentUpdate(FormData formData, FormStatus successFlag);

    FormData getLatestRecord(String merchantId, String customerId, String candidateId);

    FormData getRecord(String refId);

    String saveDataWithAddon(FormData formData);

    boolean validateFormDataRequest(FormData formData);
}
