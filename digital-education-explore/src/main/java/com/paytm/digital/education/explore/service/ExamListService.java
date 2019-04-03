package com.paytm.digital.education.explore.service;

import com.paytm.digital.education.explore.response.dto.detail.ExamInfo;

import java.util.List;

public interface ExamListService {
    List<ExamInfo> getExamList(long instituteId);
}
