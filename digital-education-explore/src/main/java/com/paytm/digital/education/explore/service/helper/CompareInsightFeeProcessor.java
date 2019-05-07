package com.paytm.digital.education.explore.service.helper;

import static com.paytm.digital.education.explore.constants.CompareConstants.AND_STRING;
import static com.paytm.digital.education.explore.constants.CompareConstants.ARE_ALMOST_SAME;
import static com.paytm.digital.education.explore.constants.CompareConstants.ARE_THE_SAME;
import static com.paytm.digital.education.explore.constants.CompareConstants.COURSE_FEES;
import static com.paytm.digital.education.explore.constants.CompareConstants.FEES_FOR;
import static com.paytm.digital.education.explore.constants.CompareConstants.LOWER_COMPARED_TO;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_ID;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

        for (Institute institute : instituteList) {
            instituteIdNameMap.put(institute.getInstituteId(), institute.getOfficialName());
            List<Course> courses = instituteCoursesMap.get(institute.getInstituteId().intValue());
            if (!CollectionUtils.isEmpty(courses)) {
                Long minFee = CompareUtil.getMinCourseFee(courses);
                if (Objects.nonNull(minFee)) {
                    instituteIdFeeMap.put(institute.getInstituteId(), minFee);
                }
            }
        }

        return getInsightMessages(instituteIdNameMap, instituteIdFeeMap);
    }

    private List<String> getInsightMessages(Map<Long, String> instituteIdNameMap, Map<Long, Long> instituteIdFeeMap) {
        List<String> result = new ArrayList<>();
        List<Long> allFees = new ArrayList<>(instituteIdFeeMap.values());
        List<String> instituteNames = new ArrayList<>(instituteIdNameMap.values());
        if (!CollectionUtils.isEmpty(instituteIdFeeMap)) {
            Long minFeeInstituteId = getMinFeeInstituteId(instituteIdFeeMap);
            if (Objects.nonNull(minFeeInstituteId)) {
                result.add(getMinFeeInsightMessage(minFeeInstituteId, instituteIdNameMap));
            } else if (areFeesAlmostSame(allFees)) {
                result.add(getAlmostSameFeeInsight(instituteNames));
            } else if (areFeesSame(allFees)) {
                result.add(getSameFeesInsight(instituteNames));
            } else {
                result.addAll(getMultipleInsightsForFee(instituteIdNameMap, instituteIdFeeMap));
            }
        }
        return result;
    }

    //This function is used to find multiple insights(sam fees, almost same fees) between every two institute
    private List<String> getMultipleInsightsForFee(Map<Long, String> instituteIdNameMap,
            Map<Long, Long> instituteIdFeeMap) {
        List<Long> instituteIds = new ArrayList<>(instituteIdFeeMap.keySet());
        int instituteSize = instituteIds.size();
        List<String> result = new ArrayList<>();
        for (int i = 0; i < instituteSize; i++) {
            if (areFeesAlmostSame(instituteIdFeeMap.get(instituteIds.get(i)),
                    instituteIdFeeMap.get(instituteIds.get((i + 1) % instituteSize)))) {
                result.add(getAlmostSameFeeInsight(Arrays.asList(instituteIdNameMap.get(instituteIds.get(i)),
                        instituteIdNameMap.get(instituteIds.get((i + 1) % instituteSize)))));
            } else if (areFeesSame(instituteIdFeeMap.get(instituteIds.get(i)),
                    instituteIdFeeMap.get(instituteIds.get((i + 1) % instituteSize)))) {
                result.add(getSameFeesInsight(Arrays.asList(instituteIdNameMap.get(instituteIds.get(i)),
                        instituteIdNameMap.get(instituteIds.get((i + 1) % instituteSize)))));
            }
        }
        return result;
    }

    private String getMinFeeInsightMessage(Long minFeeInstituteId, Map<Long, String> instituteIdNameMap) {
        StringBuilder message = new StringBuilder();
        message.append(FEES_FOR).append(instituteIdNameMap.get(minFeeInstituteId)).append(LOWER_COMPARED_TO);
        for (Long instituteId : instituteIdNameMap.keySet()) {
            if (instituteId != minFeeInstituteId) {
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

    private String getSameFeesInsight(List<String> instituteNames) {
        StringBuilder message = new StringBuilder();
        if (!CollectionUtils.isEmpty(instituteNames)) {
            message.append(FEES_FOR).append(instituteNames.get(0));
            for (int i = 1; i < instituteNames.size(); i++) {
                message.append(AND_STRING).append(instituteNames.get(i));
            }
            message.append(ARE_THE_SAME);
        }
        return message.toString();
    }

    private Long getMinFeeInstituteId(Map<Long, Long> instituteIdFeeMap) {
        Long minFee = null;
        Long minFeeInstituteId = null;
        for (Long instituteId : instituteIdFeeMap.keySet()) {
            if (minFee == null || (minFee < instituteIdFeeMap.get(instituteId) && !areFeesAlmostSame(minFee,
                    instituteIdFeeMap.get(instituteId)))) {
                minFee = instituteIdFeeMap.get(instituteId);
                minFeeInstituteId = instituteId;
            }
        }
        return minFeeInstituteId;
    }

    // This function checks if difference of fees among institutes are <= 10% - almost same
    private boolean areFeesAlmostSame(List<Long> fees) {
        int size = fees.size();
        boolean flag = true;
        for (int i = 0; i < size; i++) {
            flag = flag && areFeesAlmostSame(fees.get(i), fees.get((i + 1) % size));
        }
        return flag;
    }

    private boolean areFeesSame(List<Long> fees) {
        int size = fees.size();
        boolean flag = true;
        for (int i = 0; i < size; i++) {
            flag = flag && areFeesSame(fees.get(i), fees.get((i + 1) % size));
        }
        return flag;
    }

    //This function checks if diference of fees between two institues are 10 percent
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
