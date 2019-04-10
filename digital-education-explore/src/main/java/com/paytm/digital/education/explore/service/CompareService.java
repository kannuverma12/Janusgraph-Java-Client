package com.paytm.digital.education.explore.service;

import com.paytm.digital.education.explore.response.dto.detail.ExamDetail;
import com.paytm.digital.education.explore.response.dto.detail.InstituteComparison;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.TimeoutException;

public interface CompareService {
    InstituteComparison compareInstitutes(Long institute1, Long institute2, String fieldGroup, List<String> fields) throws IOException, TimeoutException;

}
