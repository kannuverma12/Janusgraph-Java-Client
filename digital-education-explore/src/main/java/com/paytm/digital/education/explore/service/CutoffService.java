package com.paytm.digital.education.explore.service;

import com.paytm.digital.education.explore.enums.Gender;

import com.paytm.digital.education.explore.response.dto.detail.ExamAndCutOff;
import com.paytm.digital.education.explore.response.dto.search.CutoffSearchResponse;

import java.util.List;

public interface CutoffService {
    List<CutoffSearchResponse> searchCutOffs(long instituteId, long examId, Gender gender,
            String casteGroup, String fieldGroup);

    ExamAndCutOff getSearchList(long instituteId, long examId);
}
