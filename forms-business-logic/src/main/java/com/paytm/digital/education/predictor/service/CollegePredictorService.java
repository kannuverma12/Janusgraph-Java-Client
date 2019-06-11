package com.paytm.digital.education.predictor.service;

import com.paytm.digital.education.form.model.FormData;
import com.paytm.digital.education.predictor.response.PredictorListResponse;

import java.util.Map;

public interface CollegePredictorService {

    Map<String, Object> savePredictorFormData(FormData formData);

    PredictorListResponse getPredictorList();
}
