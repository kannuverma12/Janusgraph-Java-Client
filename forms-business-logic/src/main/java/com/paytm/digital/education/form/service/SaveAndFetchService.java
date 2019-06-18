package com.paytm.digital.education.form.service;

import com.paytm.digital.education.form.model.FormData;
import com.paytm.digital.education.form.model.FormStatus;
import com.paytm.digital.education.form.model.LatestFormData;

import java.util.List;

public interface SaveAndFetchService {

    String saveData(FormData formData, boolean confirmFlag);

    String paymentUpdate(FormData formData, FormStatus successFlag);

    FormData getLatestRecord(String merchantId, String customerId, String candidateId);

    FormData getRecord(String refId);

    String saveDataWithAddon(FormData formData);

    boolean validateFormDataRequest(FormData formData);

    LatestFormData getCurrentOpenAndLastPaidFormDetails(String merchantId, String customerId,
                                                        String candidateId, List<String> keys);
}
