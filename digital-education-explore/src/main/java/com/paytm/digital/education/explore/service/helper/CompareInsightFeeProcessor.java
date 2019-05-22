package com.paytm.digital.education.explore.service.helper;

import static com.paytm.digital.education.explore.constants.CompareConstants.AND_STRING;
import static com.paytm.digital.education.explore.constants.CompareConstants.ARE_ALMOST_SAME;
import static com.paytm.digital.education.explore.constants.CompareConstants.COURSE_FEES;
import static com.paytm.digital.education.explore.constants.CompareConstants.FEES_FOR;
import static com.paytm.digital.education.explore.constants.CompareConstants.LOWER_COMPARED_TO;
import static com.paytm.digital.education.explore.constants.CompareConstants.NO_OF_INSTITUTES_WITH_MIN_FEE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_ID;
import static com.paytm.digital.education.explore.utility.CompareUtil.getInstituteName;

import com.paytm.digital.education.explore.database.entity.Course;
import com.paytm.digital.education.explore.database.entity.Institute;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.utility.CompareUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompareInsightFeeProcessor {

    private CommonMongoRepository commonMongoRepository;

    public List<String> getComparativeInsights(List<Institute> instituteList) {
        Map<Long, Long> instituteIdFeeMap = new HashMap<>();
        Map<Long, String> instituteIdNameMap = new HashMap<>();
        List<String> courseQueryFields = Arrays.asList(COURSE_FEES, INSTITUTE_ID);
        List<Long> instituteIds =
                instituteList.stream().map(institute -> institute.getInstituteId()).collect(Collectors.toList());
        Map<Integer, List<Course>> instituteCoursesMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(courseQueryFields)) {
            List<Course> courses = commonMongoRepository
                    .getEntityFieldsByValuesIn(INSTITUTE_ID, instituteIds, Course.class,
                            courseQueryFields);
            if (!CollectionUtils.isEmpty(courses)) {
                instituteCoursesMap = courses.stream().filter(c -> Objects.nonNull(c.getInstitutionId()))
                        .collect(Collectors.groupingBy(c -> c.getInstitutionId(),
                                Collectors.mapping(c -> c, Collectors.toList())));
            }
        }
        long minimumOfMinFee = Long.MAX_VALUE;
        long maximumOfMinFee = Long.MIN_VALUE;
        for (Institute institute : instituteList) {
            List<Course> courses = instituteCoursesMap.get(institute.getInstituteId().intValue());
            if (!CollectionUtils.isEmpty(courses)) {
                Long minFee = CompareUtil.getMinCourseFee(courses);
                if (Objects.nonNull(minFee)) {
                    instituteIdNameMap.put(institute.getInstituteId(), getInstituteName(institute));
                    instituteIdFeeMap.put(institute.getInstituteId(), minFee);
                    minimumOfMinFee = minimumOfMinFee > minFee ? minFee : minimumOfMinFee;
                    maximumOfMinFee = maximumOfMinFee < minFee ? minFee : maximumOfMinFee;
                }
            }
        }
        if (instituteIdFeeMap.keySet().size() > 1) {
            return getInsightMessages(instituteIdNameMap, instituteIdFeeMap, maximumOfMinFee,
                    minimumOfMinFee);
        }
        return null;
    }

    private List<String> getInsightMessages(Map<Long, String> instituteIdNameMap,
            Map<Long, Long> instituteIdFeeMap, long maxFee, long minFee) {
        List<String> result = new ArrayList<>();
        List<String> instituteNames = new ArrayList<>(instituteIdNameMap.values());
        if (!CollectionUtils.isEmpty(instituteIdFeeMap)) {
            if (areFeesAlmostSame(maxFee, minFee)) {
                result.add(getAlmostSameFeeInsight(instituteNames));
            } else {
                Map<String,Long> minFeeInstituteInfo = getMinFeeInfo(instituteIdFeeMap);
                Long minFeeInstituteId = minFeeInstituteInfo.get(INSTITUTE_ID);
                Long minFeeInstituteCount = minFeeInstituteInfo.get(NO_OF_INSTITUTES_WITH_MIN_FEE);
                if (minFeeInstituteCount == 1 && Objects.nonNull(minFeeInstituteId)) {
                    Set<Long> instituteIds = instituteIdNameMap.keySet();
                    result.add(getMinFeeInsightMessage(minFeeInstituteId, instituteIdNameMap, instituteIds));
                }  else {
                    result.addAll(getMultipleInsightsForFee(instituteIdNameMap, instituteIdFeeMap));
                }
            }
        }
        return result;
    }

    /*This function is used to find multiple insights(same fees, almost same fees) between every
    ** two institute
    */
    private List<String> getMultipleInsightsForFee(Map<Long, String> instituteIdNameMap,
            Map<Long, Long> instituteIdFeeMap) {
        List<Long> instituteIds = new ArrayList<>(instituteIdFeeMap.keySet());
        int instituteSize = instituteIds.size();
        List<String> result = new ArrayList<>();
        for (int i = 0; i < instituteSize; i++) {
            int next = (i + 1) % instituteSize;
            if (areFeesAlmostSame(instituteIdFeeMap.get(instituteIds.get(i)),
                    instituteIdFeeMap.get(instituteIds.get(next)))) {
                result.add(getAlmostSameFeeInsight(Arrays.asList(instituteIdNameMap.get(instituteIds.get(i)),
                        instituteIdNameMap.get(instituteIds.get(next)))));
            } else  {
                long minFeeInstituteId = 0;
                minFeeInstituteId = instituteIdFeeMap.get(instituteIds.get(i))
                        > instituteIdFeeMap.get(instituteIds.get(next)) ? instituteIds.get(next)
                        : instituteIds.get(i);
                List<Long> ids = Arrays.asList(instituteIds.get(i), instituteIds.get(next));
                result.add(getMinFeeInsightMessage(minFeeInstituteId, instituteIdNameMap, ids));
            }
        }
        return result;
    }

    private String getMinFeeInsightMessage(Long minFeeInstituteId,
            Map<Long, String> instituteIdNameMap, Collection<Long> instituteIds) {
        StringBuilder message = new StringBuilder();
        message.append(FEES_FOR).append(instituteIdNameMap.get(minFeeInstituteId)).append(LOWER_COMPARED_TO);
        for (Long instituteId : instituteIds) {
            if (!instituteId.equals(minFeeInstituteId)) {
                message.append(instituteIdNameMap.get(instituteId)).append(AND_STRING);
            }
        }
        return StringUtils.substringBeforeLast(message.toString(), AND_STRING);
    }

    private String getAlmostSameFeeInsight(List<String> instituteNames) {
        StringBuilder message = new StringBuilder();
        if (!CollectionUtils.isEmpty(instituteNames)) {
            message.append(FEES_FOR).append(instituteNames.get(0));
            for (int i = 1; i < instituteNames.size(); i++) {
                message.append(AND_STRING).append(instituteNames.get(i));
            }
            message.append(ARE_ALMOST_SAME);
        }
        return message.toString();
    }

    private Map<String,Long> getMinFeeInfo(Map<Long, Long> instituteIdFeeMap) {
        Long minFee = null;
        Long minFeeInstituteId = null;
        int noOfInstituteWithMinFee = 0;
        for (Long instituteId : instituteIdFeeMap.keySet()) {
            if (minFee == null || minFee >= instituteIdFeeMap.get(instituteId)) {
                if (minFee == null || !areFeesAlmostSame(minFee, instituteIdFeeMap.get(instituteId))) {
                    minFee = instituteIdFeeMap.get(instituteId);
                    minFeeInstituteId = instituteId;
                    noOfInstituteWithMinFee = 1;
                } else {
                    noOfInstituteWithMinFee++;
                }
            }
        }
        Map<String, Long> minFeeInstitutesInfo = new HashMap<>();
        minFeeInstitutesInfo.put(INSTITUTE_ID, minFeeInstituteId);
        minFeeInstitutesInfo.put(NO_OF_INSTITUTES_WITH_MIN_FEE, (long)noOfInstituteWithMinFee);
        return minFeeInstitutesInfo;
    }

    //This function checks if difference of fees between two institutes are 10 percent
    private boolean areFeesAlmostSame(Long fee1, Long fee2) {
        long diff = Math.abs(fee1 - fee2);
        long denom = fee1 < fee2 ? fee1 : fee2;
        long percentage = diff * 100 / denom;
        if (percentage <= 10) {
            return true;
        }
        return false;
    }

    private boolean areFeesSame(long fee1, long fee2) {
        return fee1 == fee2;
    }
}
