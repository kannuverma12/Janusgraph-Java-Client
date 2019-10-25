package com.paytm.digital.education.admin.service;

import com.paytm.digital.education.admin.request.RankingsRequest;
import com.paytm.digital.education.admin.response.RankingResponse;
import com.paytm.digital.education.enums.EducationEntity;

public interface RankingService {
    RankingResponse updateRankings(RankingsRequest rankingsRequest);

    RankingResponse getPaytmRankings(EducationEntity entity);
}
