package com.paytm.digital.education.form.service;

import com.paytm.digital.education.form.model.FormData;
import com.paytm.digital.education.form.response.PredictorListResponse;

import java.util.Map;

public interface CollegePredictorService {

    Map<String, Object> savePredictorFormData(FormData formData);

    PredictorListResponse getPredictorList();
}
