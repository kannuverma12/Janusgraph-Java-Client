package com.paytm.digital.education.explore.service;

import com.paytm.digital.education.explore.response.dto.detail.CompareDetail;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

public interface CompareService {
    CompareDetail compareInstitutes(List<Long> instList, String fieldGroup, List<String> fields) throws IOException, TimeoutException;

}
