package com.paytm.digital.education.explore.service.helper;

import static com.paytm.digital.education.enums.Number.ONE;
import static com.paytm.digital.education.enums.Number.THREE;
import static com.paytm.digital.education.enums.Number.TWO;
import static com.paytm.digital.education.explore.constants.CompareConstants.AND_STRING;
import static com.paytm.digital.education.explore.constants.CompareConstants.AS_PER;
import static com.paytm.digital.education.explore.constants.CompareConstants.BY;
import static com.paytm.digital.education.explore.constants.CompareConstants.CAREERS360;
import static com.paytm.digital.education.explore.constants.CompareConstants.COMPARE_CACHE_NAMESPACE;
import static com.paytm.digital.education.explore.constants.CompareConstants.HAS_BEEN_RANKED;
import static com.paytm.digital.education.explore.constants.CompareConstants.IS_RANKED;
import static com.paytm.digital.education.explore.constants.CompareConstants.IS_RANKED_HIGHER;
import static com.paytm.digital.education.explore.constants.CompareConstants.IS_RATED;
import static com.paytm.digital.education.explore.constants.CompareConstants.NIRF;

import com.paytm.digital.education.explore.database.entity.Institute;
import com.paytm.digital.education.explore.response.dto.detail.Ranking;
import com.paytm.digital.education.explore.utility.CompareUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompareInsightRankProcessor {

    public List<String> getComparativeInsights(List<Institute> instituteList) {
        int instituteSize = instituteList.size();
        Map<String, Ranking> rankingDataMap1 = CompareUtil.getResponseRankingMap(instituteList.get(0).getRankings());
        Map<String, Ranking> rankingDataMap2 = null;
        Map<String, Ranking> rankingDataMap3 = null;
        if (instituteSize == ONE.getValue()) {
            return getRankingInsightForOneInstitute(rankingDataMap1, instituteList.get(0));
        } else if (instituteSize == TWO.getValue()) {
            rankingDataMap2 = CompareUtil.getResponseRankingMap(instituteList.get(1).getRankings());
        } else if (instituteSize == THREE.getValue()) {
            rankingDataMap2 = CompareUtil.getResponseRankingMap(instituteList.get(1).getRankings());
            rankingDataMap3 = CompareUtil.getResponseRankingMap(instituteList.get(2).getRankings());
        }

        Set<String> commonKeys = getCommonKeys(instituteSize, rankingDataMap1, rankingDataMap2, rankingDataMap3);
        if (!CollectionUtils.isEmpty(commonKeys)) {
            List<String> rankingCommonInsights =
                    getCommonInsight(instituteSize, commonKeys, instituteList, rankingDataMap1, rankingDataMap2,
                            rankingDataMap3);
            if (!commonKeys.contains(NIRF)) {
                List<String> nirfInsight =
                        getPartialNIRFInsight(instituteSize, instituteList, rankingDataMap1, rankingDataMap2,
                                rankingDataMap3);
                if (!nirfInsight.isEmpty()) {
                    rankingCommonInsights.addAll(nirfInsight);
                }
            }
            return rankingCommonInsights;
        }

        return getMultipleInsights(instituteSize, instituteList, rankingDataMap1, rankingDataMap2, rankingDataMap3);
    }

    @Cacheable(value = COMPARE_CACHE_NAMESPACE, key = "{'rank_'+#rankingSource+'_'+#institute1.instituteId+'_'"
            + "+#institute2.instituteId, 'rank_'+#rankingSource+'_'+#institute2.instituteId+'_'"
            + "+#institute1.instituteId}")
    public String getInsightBetweenTwoInstitutes(String rankingSource, Map<String, Ranking> rankingDataMap1,
            Map<String, Ranking> rankingDataMap2, Institute institute1, Institute institute2) {
        if (rankingDataMap1.get(rankingSource).getRank() > rankingDataMap2.get(rankingSource).getRank()) {
            return institute1.getOfficialName() + IS_RANKED_HIGHER + institute2.getOfficialName();
        } else {
            return institute2.getOfficialName() + IS_RANKED_HIGHER + institute1.getOfficialName();
        }
    }

    @Cacheable(value = COMPARE_CACHE_NAMESPACE, key = "'rank_'+#institute.instituteId")
    public List<String> getRankingInsightForOneInstitute(Map<String, Ranking> rankingDataMap, Institute institute) {
        List<String> result = new ArrayList<>();
        if (rankingDataMap.containsKey(NIRF)) {
            result.add(institute.getOfficialName() + HAS_BEEN_RANKED + rankingDataMap.get(NIRF).getRank() + BY + NIRF);
        } else if (rankingDataMap.containsKey(CAREERS360)) {
            result.add(institute.getOfficialName() + HAS_BEEN_RANKED + rankingDataMap.get(CAREERS360).getRank()
                    + BY + CAREERS360);
        }
        return result;
    }

    private List<String> getCommonInsight(int instituteSize, Set<String> commonKeys, List<Institute> instituteList,
            Map<String, Ranking>... rankingDataMaps) {
        List<String> result = new ArrayList<>();
        List<String> instituteNames = instituteList.stream().map(institute -> institute.getOfficialName()).collect(
                Collectors.toList());
        for (String commonKey : commonKeys) {
            Integer maxRank = rankingDataMaps[0].get(commonKey).getRank();
            Double maxScore = rankingDataMaps[0].get(commonKey).getScore();
            boolean rankPresent = (maxRank != null);
            boolean scorePresent = (maxScore != null);
            int maxIndex = 0;
            for (int i = 1; i < instituteSize; i++) {
                if (rankPresent && rankingDataMaps[i].get(commonKey).getRank() != null) {
                    if (rankingDataMaps[i].get(commonKey).getRank() > maxRank) {
                        maxRank = rankingDataMaps[i].get(commonKey).getRank();
                        maxIndex = i;
                    }
                    if (rankingDataMaps[i].get(commonKey).getScore() == null) {
                        scorePresent = false;
                    } else if (scorePresent
                            && Double.compare(maxScore, rankingDataMaps[i].get(commonKey).getScore()) < 0) {
                        maxScore = rankingDataMaps[i].get(commonKey).getScore();
                    }
                } else if (scorePresent && rankingDataMaps[i].get(commonKey).getScore() != null) {
                    if (Double.compare(maxScore, rankingDataMaps[i].get(commonKey).getScore()) < 0) {
                        maxScore = rankingDataMaps[i].get(commonKey).getScore();
                        maxIndex = i;
                        rankPresent = false;
                    }
                }
            }
            result.add(getCommonInsightMessage(instituteSize, maxIndex, instituteNames, commonKey));
        }
        return result;
    }


    private String getCommonInsightMessage(int instituteSize, int maxIndex, List<String> instituteNames,
            String rankingSource) {
        StringBuilder message = new StringBuilder();
        message.append(instituteNames.get(maxIndex)).append(IS_RANKED_HIGHER)
                .append(instituteNames.get((maxIndex + 1) % instituteSize));
        if (instituteSize > TWO.getValue()) {
            for (int i = 2; i < instituteSize; i++) {
                message.append(AND_STRING).append(instituteNames.get((maxIndex + i) % instituteSize));
            }
        }
        message.append(BY).append(rankingSource);
        return message.toString();
    }

    private Set<String> getCommonKeys(int instituteSize, Map<String, Ranking>... rankingDataMaps) {
        Set<String> commonKeys = rankingDataMaps[0].keySet();
        for (int i = 1; i < instituteSize; i++) {
            commonKeys.retainAll(rankingDataMaps[i].keySet());
        }
        return commonKeys;
    }

    private List<String> getMultipleInsights(int instituteSize, List<Institute> instituteList,
            Map<String, Ranking>... rankingDataMaps) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < instituteSize; i++) {
            Set<String> commonKeys = rankingDataMaps[i].keySet();
            commonKeys.retainAll(rankingDataMaps[(i + 1) % instituteSize].keySet());
            if (!CollectionUtils.isEmpty(commonKeys)) {
                for (String rankingSource : commonKeys) {
                    result.add(getInsightBetweenTwoInstitutes(rankingSource, rankingDataMaps[i],
                            rankingDataMaps[(i + 1) % instituteSize], instituteList.get(i),
                            instituteList.get((i + 1) % instituteSize)));
                }
            }
        }
        return result;
    }

    private List<String> getPartialNIRFInsight(int instituteSize, List<Institute> instituteList,
            Map<String, Ranking>... rankingDataMaps) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < instituteSize; i++) {
            if (rankingDataMaps[i].containsKey(NIRF) && rankingDataMaps[(i + 1) % instituteSize].containsKey(NIRF)) {
                result.add(getInsightBetweenTwoInstitutes(NIRF, rankingDataMaps[i],
                        rankingDataMaps[(i + 1) % instituteSize], instituteList.get(i),
                        instituteList.get((i + 1) % instituteSize)));
            }
        }
        //check for single institutes
        if (result.isEmpty()) {
            for (int i = 0; i < instituteSize; i++) {
                if (rankingDataMaps[i].containsKey(NIRF)) {
                    if (rankingDataMaps[i].get(NIRF).getRank() != null) {
                        result.add(instituteList.get(i).getOfficialName() + IS_RANKED + rankingDataMaps[i].get(NIRF)
                                .getRank() + AS_PER + NIRF);
                    } else if (StringUtils.isNotBlank(rankingDataMaps[i].get(NIRF).getRating())) {
                        result.add(instituteList.get(i).getOfficialName() + IS_RATED + rankingDataMaps[i].get(NIRF)
                                .getRating() + AS_PER + NIRF);
                    }
                }
            }
        }
        return result;
    }

}