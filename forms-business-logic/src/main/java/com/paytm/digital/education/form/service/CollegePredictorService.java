package com.paytm.digital.education.form.service;

import com.paytm.digital.education.form.model.FormData;

import java.util.Map;

public interface CollegePredictorService {

    public Map<String, Object> savePredictorFormData(FormData formData);
}
