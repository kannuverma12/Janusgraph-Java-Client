package com.paytm.digital.education.explore.service.helper;

import static com.paytm.digital.education.enums.Number.ONE;
import static com.paytm.digital.education.enums.Number.THREE;
import static com.paytm.digital.education.enums.Number.TWO;
import static com.paytm.digital.education.explore.constants.CompareConstants.AND_STRING;
import static com.paytm.digital.education.explore.constants.CompareConstants.AVERAGE;
import static com.paytm.digital.education.explore.constants.CompareConstants.COMPARE_CACHE_NAMESPACE;
import static com.paytm.digital.education.explore.constants.CompareConstants.HAS_AVERAGE_PLACEMENT_OF;
import static com.paytm.digital.education.explore.constants.CompareConstants.HAS_MAXIMUM_PLACEMENT_OF;
import static com.paytm.digital.education.explore.constants.CompareConstants.HAS_MEDIAN_PLACEMENT_OF;
import static com.paytm.digital.education.explore.constants.CompareConstants.HAS_MINIMUM_PLACEMENT_OF;
import static com.paytm.digital.education.explore.constants.CompareConstants.IS_HIGHER_THAN;
import static com.paytm.digital.education.explore.constants.CompareConstants.MAXIMUM;
import static com.paytm.digital.education.explore.constants.CompareConstants.MEDIAN;
import static com.paytm.digital.education.explore.constants.CompareConstants.MINIMUM;
import static com.paytm.digital.education.explore.constants.CompareConstants.PLACEMENTS_OF;

import com.paytm.digital.education.explore.database.entity.Institute;
import com.paytm.digital.education.explore.database.entity.Placement;
import com.paytm.digital.education.explore.utility.CommonUtil;
import com.paytm.digital.education.utility.DateUtil;
import javafx.util.Pair;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CompareInsightPlacementProcessor {

    public List<String> getComparativeInsights(List<Institute> instituteList) {
        int size = instituteList.size();
        Map<String, Placement> placementMap1 = getPlacementData(instituteList.get(0));
        Map<String, Placement> placementMap2 = null;
        Map<String, Placement> placementMap3 = null;
        if (size == ONE.getValue()) {
            return getPlacementInsightForOneInstitute(placementMap1, instituteList.get(0));
        } else if (size == TWO.getValue()) {
            placementMap2 = getPlacementData(instituteList.get(1));
        } else if (size == THREE.getValue()) {
            placementMap2 = getPlacementData(instituteList.get(1));
            placementMap3 = getPlacementData(instituteList.get(2));
        }
        Pair<Integer, String> commonMaxIndex = getIndexForCommonData(size, placementMap1, placementMap2, placementMap3);

        List<String> instituteNames =
                instituteList.stream().map(institute -> institute.getOfficialName()).collect(Collectors.toList());
        if (commonMaxIndex.getKey() != -1) {
            return Arrays.asList(getCommonInsightMessage(commonMaxIndex.getKey(), size, commonMaxIndex.getValue(),
                    instituteNames));
        }
        return getMultipleInsights(size, instituteList, placementMap1, placementMap2, placementMap3);
    }

    @Cacheable(value = COMPARE_CACHE_NAMESPACE, key = "'placement_'+#institute.instituteId")
    public List<String> getPlacementInsightForOneInstitute(
            Map<String, Placement> placementDataMap,
            Institute institute) {
        if (placementDataMap.containsKey(MEDIAN)) {
            return Arrays.asList(institute.getOfficialName() + HAS_MEDIAN_PLACEMENT_OF + placementDataMap
                    .get(MEDIAN).getMedian());
        } else if (placementDataMap.containsKey(AVERAGE)) {
            return Arrays.asList(institute.getOfficialName() + HAS_AVERAGE_PLACEMENT_OF + placementDataMap
                    .get(AVERAGE).getAverage());
        } else if (placementDataMap.containsKey(MAXIMUM)) {
            return Arrays.asList(institute.getOfficialName() + HAS_MAXIMUM_PLACEMENT_OF + placementDataMap
                    .get(MAXIMUM).getMaximum());
        } else if (placementDataMap.containsKey(MINIMUM)) {
            return Arrays.asList(institute.getOfficialName() + HAS_MINIMUM_PLACEMENT_OF + placementDataMap
                    .get(MINIMUM).getMinimum());
        }
        return null;
    }

    @Cacheable(value = COMPARE_CACHE_NAMESPACE, key = "{'placement_'+#institute1.instituteId+'_'"
            + "+#institute2.instituteId, 'placement_'+#institute2.instituteId+'_'+#institute1.instituteId}")
    public String getPlacementInsightsBetweenTwoInstitutes(Set<String> commonKeys,
            Map<String, Placement> placementDataMap1, Map<String, Placement> placementDataMap2, Institute institute1,
            Institute institute2) {
        String firstInstitute = institute1.getOfficialName();
        String secondInstitute = institute2.getOfficialName();
        if (commonKeys.contains(MEDIAN)) {
            return getInsightMessageForTwoInstitutes(MEDIAN, placementDataMap1.get(MEDIAN).getMedian(),
                    placementDataMap2.get(MEDIAN).getMedian(), firstInstitute, secondInstitute);
        } else if (commonKeys.contains(AVERAGE)) {
            return getInsightMessageForTwoInstitutes(AVERAGE, placementDataMap1.get(AVERAGE).getAverage(),
                    placementDataMap2.get(AVERAGE).getAverage(), firstInstitute, secondInstitute);
        } else if (commonKeys.contains(MAXIMUM)) {
            return getInsightMessageForTwoInstitutes(MAXIMUM, placementDataMap1.get(MAXIMUM).getMaximum(),
                    placementDataMap2.get(MAXIMUM).getMaximum(), firstInstitute, secondInstitute);
        } else if (commonKeys.contains(MINIMUM)) {
            return getInsightMessageForTwoInstitutes(MINIMUM, placementDataMap1.get(MINIMUM).getMinimum(),
                    placementDataMap2.get(MINIMUM).getMinimum(), firstInstitute, secondInstitute);
        }
        return null;
    }

    private Pair<Integer, String> getIndexForCommonData(int size,
            Map<String, Placement>... placementDataMaps) {
        //Get common placement data - in which common data(MEDIAN/AVERAGE/MAXIMUM/MINIMUM) exists
        Set<String> commonKeys = placementDataMaps[0].keySet();
        for (int i = 1; i < size; i++) {
            commonKeys.retainAll(placementDataMaps[i].keySet());
        }
        if (!CollectionUtils.isEmpty(commonKeys)) {
            if (commonKeys.contains(MEDIAN)) {
                List<Integer> medianSalaries =
                        Arrays.stream(placementDataMaps).filter(placementMap -> Objects.nonNull(placementMap))
                                .map(placementMap -> placementMap.get(MEDIAN).getMedian()).collect(Collectors.toList());
                int maxIndex = CommonUtil.getIndexForMaxValue(medianSalaries);
                return new Pair<>(maxIndex, MEDIAN);
            }
            if (commonKeys.contains(AVERAGE)) {
                List<Integer> averageSalaries =
                        Arrays.stream(placementDataMaps).filter(placementMap -> Objects.nonNull(placementMap))
                                .map(placementMap -> placementMap.get(AVERAGE).getAverage())
                                .collect(Collectors.toList());
                int maxIndex = CommonUtil.getIndexForMaxValue(averageSalaries);
                return new Pair<>(maxIndex, AVERAGE);
            }
            if (commonKeys.contains(MAXIMUM)) {
                List<Integer> maxSalaries =
                        Arrays.stream(placementDataMaps).filter(placementMap -> Objects.nonNull(placementMap))
                                .map(placementMap -> placementMap.get(MAXIMUM).getMaximum())
                                .collect(Collectors.toList());
                int maxIndex = CommonUtil.getIndexForMaxValue(maxSalaries);
                return new Pair<>(maxIndex, MAXIMUM);
            }
            if (commonKeys.contains(MINIMUM)) {
                List<Integer> minSalaries =
                        Arrays.stream(placementDataMaps).filter(placementMap -> Objects.nonNull(placementMap))
                                .map(placementMap -> placementMap.get(MINIMUM).getMinimum())
                                .collect(Collectors.toList());
                int maxIndex = CommonUtil.getIndexForMaxValue(minSalaries);
                return new Pair<>(maxIndex, MINIMUM);
            }
        }

        return new Pair<>(-1, null);
    }

    private String getCommonInsightMessage(int maxIndex, int size, String commonKey, List<String> instituteNames) {
        if (size == THREE.getValue()) {
            return commonKey + PLACEMENTS_OF + instituteNames.get(maxIndex) + IS_HIGHER_THAN + instituteNames
                    .get((maxIndex + 1) % size) + AND_STRING + instituteNames.get((maxIndex + 2) % size);
        } else {
            return commonKey + PLACEMENTS_OF + instituteNames.get(maxIndex) + IS_HIGHER_THAN + instituteNames
                    .get((maxIndex + 1) % size);
        }
    }

    private String getInsightMessageForTwoInstitutes(String salaryType, Integer firstSalary, Integer secondSalary,
            String firstInstitute, String secondInstitute) {
        if (firstSalary > secondSalary) {
            return salaryType + PLACEMENTS_OF + firstInstitute + IS_HIGHER_THAN + secondInstitute;
        }
        return salaryType + PLACEMENTS_OF + secondInstitute + IS_HIGHER_THAN + firstInstitute;
    }

    private List<String> getMultipleInsights(int size, List<Institute> instituteList,
            Map<String, Placement>... placementDataMaps) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Set<String> commonKeys = placementDataMaps[i].keySet();
            commonKeys.retainAll(placementDataMaps[(i + 1) % size].keySet());
            if (!CollectionUtils.isEmpty(commonKeys)) {
                String message = getPlacementInsightsBetweenTwoInstitutes(commonKeys, placementDataMaps[i],
                        placementDataMaps[(i + 1) % size], instituteList.get(i), instituteList.get((i + 1) % size));
                if (Objects.nonNull(message)) {
                    result.add(message);
                }
            }
        }
        return result;
    }

    private Map<String, Placement> getPlacementData(
            Institute institute) {
        if (institute == null || CollectionUtils.isEmpty(institute.getSalariesPlacement())) {
            return Collections.EMPTY_MAP;
        }

        int latestYear = DateUtil.getCurrentYear() - 2;
        int medianSalary = Integer.MIN_VALUE;
        int averageSalary = Integer.MIN_VALUE;
        int maximumSalary = Integer.MIN_VALUE;
        int minimumSalary = Integer.MIN_VALUE;
        Map<String, Placement> placementDataMap = new HashMap<>(4);
        for (com.paytm.digital.education.explore.database.entity.Placement placement : institute
                .getSalariesPlacement()) {
            if (placement.getYear() >= latestYear) {
                latestYear = placement.getYear();
                if (placement.getMedian() != null && (medianSalary == Integer.MIN_VALUE || medianSalary < placement
                        .getMedian())) {
                    placementDataMap.put(MEDIAN, placement);
                } else if (placement.getAverage() != null && (averageSalary == Integer.MIN_VALUE
                        || averageSalary < placement.getAverage())) {
                    placementDataMap.put(AVERAGE, placement);
                } else if (placement.getMaximum() != null && (maximumSalary == Integer.MIN_VALUE
                        || maximumSalary < placement.getMaximum())) {
                    placementDataMap.put(MAXIMUM, placement);
                } else if (placement.getMinimum() != null && (minimumSalary == Integer.MIN_VALUE
                        || minimumSalary < placement.getMinimum())) {
                    placementDataMap.put(MINIMUM, placement);
                }
            }
        }
        return placementDataMap;
    }
}
