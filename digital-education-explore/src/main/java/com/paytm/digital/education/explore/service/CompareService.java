package com.paytm.digital.education.explore.service;

import com.paytm.digital.education.explore.response.dto.detail.CompareDetail;
import javafx.util.Pair;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public interface CompareService {
    CompareDetail compareInstitutes(Map<Long, String> instKeyMap, String fieldGroup,
            List<String> fields,
            Long userId) throws IOException, TimeoutException;
}
