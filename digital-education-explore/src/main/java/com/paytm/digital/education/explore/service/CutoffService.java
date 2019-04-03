package com.paytm.digital.education.explore.service;

import com.paytm.digital.education.explore.request.dto.search.CutoffSearchRequest;
import com.paytm.digital.education.explore.response.dto.detail.ExamAndCutOff;
import com.paytm.digital.education.explore.response.dto.search.CutoffSearchResponse;

import java.util.List;

public interface CutoffService {
    List<CutoffSearchResponse> searchCutOffs(CutoffSearchRequest cutoffSearchRequest);

    ExamAndCutOff getSearchList(long instituteId, long examId);
}
