package com.paytm.digital.education.explore.service;

import com.paytm.digital.education.enums.Gender;

import com.paytm.digital.education.dto.detail.ExamAndCutOff;
import com.paytm.digital.education.explore.response.dto.search.CutoffSearchResponse;

import java.util.List;

public interface CutoffService {
    List<CutoffSearchResponse> searchCutOffs(long instituteId, long examId, Gender gender,
            String casteGroup, String fieldGroup);

    ExamAndCutOff getSearchList(long instituteId, long examId);
}
