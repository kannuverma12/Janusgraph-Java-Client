package com.paytm.digital.education.explore.service.impl;

import static com.paytm.digital.education.explore.constants.CompareConstants.FEE;
import static com.paytm.digital.education.explore.constants.CompareConstants.PLACEMENT;
import static com.paytm.digital.education.explore.constants.CompareConstants.RANKING;

import com.paytm.digital.education.explore.database.entity.Institute;
import com.paytm.digital.education.explore.service.helper.CompareInsightFeeProcessor;
import com.paytm.digital.education.explore.service.helper.CompareInsightPlacementProcessor;
import com.paytm.digital.education.explore.service.helper.CompareInsightRankProcessor;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class CompareInsightsServiceImpl {

    private CompareInsightPlacementProcessor compareInsightPlacementProcessor;
    private CompareInsightRankProcessor      compareInsightRankProcessor;
    private CompareInsightFeeProcessor       compareInsightFeeProcessor;

    @Cacheable(value = "compare_insights")
    public Map<String, List<String>> getInstituteKeyInsights(List<Institute> instituteList) {
        Map<String, List<String>> insightResultMap = new HashMap<>();

        List<String> feeInsights = compareInsightFeeProcessor.getComparativeInsights(instituteList);
        if (!CollectionUtils.isEmpty(feeInsights)) {
            insightResultMap.put(FEE, feeInsights);
        }

        List<String> rankingInsights = compareInsightRankProcessor.getComparativeInsights(instituteList);
        if (!CollectionUtils.isEmpty(rankingInsights)) {
            insightResultMap.put(RANKING, rankingInsights);
        }

        List<String> placementInsights = compareInsightPlacementProcessor.getComparativeInsights(instituteList);
        if (!CollectionUtils.isEmpty(placementInsights)) {
            insightResultMap.put(PLACEMENT, placementInsights);
        }
        return insightResultMap;
    }

}
