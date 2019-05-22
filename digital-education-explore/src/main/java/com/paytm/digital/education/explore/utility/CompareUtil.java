package com.paytm.digital.education.explore.utility;

import static com.paytm.digital.education.explore.constants.CompareConstants.GENERAL;
import static com.paytm.digital.education.explore.constants.CompareConstants.INST_LATEST_YEAR;
import static com.paytm.digital.education.explore.constants.CompareConstants.UNIVERSITIES;
import static com.paytm.digital.education.explore.constants.ExploreConstants.OVERALL_RANKING;

import com.paytm.digital.education.explore.database.entity.Course;
import com.paytm.digital.education.explore.database.entity.CourseFee;
import com.paytm.digital.education.explore.database.entity.Institute;
import com.paytm.digital.education.explore.response.dto.detail.Ranking;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@UtilityClass
public class CompareUtil {

    public Map<String, Ranking> getResponseRankingMap(
            List<com.paytm.digital.education.explore.database.entity.Ranking> dbRankingList) {
        Map<String, List<Ranking>> rankingMap = new HashMap<>();
        Map<String, Ranking> retMap = new HashMap<>();

        rankingMap = dbRankingList.stream().filter(r -> {
            return (Objects.nonNull(r.getSource()) && Objects.nonNull(r.getRankingType()) && (
                    r.getRankingType().equalsIgnoreCase(UNIVERSITIES) || r.getRankingType()
                            .equalsIgnoreCase(OVERALL_RANKING)));
        }).map(r -> getRankingDTO(r)).collect(Collectors.groupingBy(r -> r.getSource(),
                Collectors.mapping(r -> r, Collectors.toList()))
        );

        for (Map.Entry<String, List<Ranking>> en : rankingMap.entrySet()) {
            String key = en.getKey();
            List<Ranking> rankingList = en.getValue();
            Ranking ranking = null;
            int latest = INST_LATEST_YEAR;
            for (Ranking r : rankingList) {
                if (Objects.nonNull(r.getYear())) {
                    if (r.getYear() > latest) {
                        latest = r.getYear();
                        ranking = r;
                    }
                }
            }
            if (Objects.nonNull(ranking)) {
                retMap.put(ranking.getSource(), ranking);
            }
        }
        return retMap;
    }

    // find minimum fee for general category
    public Long getMinCourseFee(List<Course> courses) {
        Long minFee = Long.MAX_VALUE;
        for (Course c : courses) {
            List<CourseFee> feesList = c.getCourseFees();
            if (!CollectionUtils.isEmpty(feesList)) {
                if (feesList.size() > 1) {
                    for (CourseFee fee : feesList) {
                        if (Objects.nonNull(fee.getCasteGroup()) && fee.getCasteGroup()
                                .equalsIgnoreCase(GENERAL)) {
                            if (Objects.nonNull(fee.getFee())) {
                                minFee = Math.min(minFee, fee.getFee());
                            }
                        }
                    }
                } else {
                    if (feesList.size() == 1) {
                        CourseFee fee = feesList.get(0);
                        if (Objects.nonNull(fee) && Objects.nonNull(fee.getFee())) {
                            minFee = Math.min(minFee, fee.getFee());
                        }
                    }
                }
            }
        }
        if (minFee != Long.MAX_VALUE) {
            return minFee;
        }
        return null;
    }

    private Ranking getRankingDTO(
            com.paytm.digital.education.explore.database.entity.Ranking dbRanking) {
        Ranking r = new Ranking();
        r.setRank(dbRanking.getRank());
        r.setScore(dbRanking.getScore());
        r.setSource(dbRanking.getSource());
        r.setYear(dbRanking.getYear());
        r.setRating(dbRanking.getRating());
        return r;
    }

    public static String getInstituteName(Institute institute) {
        if (StringUtils.isNotBlank(institute.getCommonName())) {
            return institute.getCommonName();
        }
        return institute.getOfficialName();
    }

}
