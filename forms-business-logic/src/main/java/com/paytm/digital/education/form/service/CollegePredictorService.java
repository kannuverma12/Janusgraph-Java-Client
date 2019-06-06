package com.paytm.digital.education.form.service;

import com.paytm.digital.education.form.model.FormData;
import com.paytm.digital.education.form.model.CollegePredictor;

import java.util.List;
import java.util.Map;

public interface CollegePredictorService {

    public Map<String, Object> savePredictorFormData(FormData formData);

    List<CollegePredictor> getPredictorList();
}
