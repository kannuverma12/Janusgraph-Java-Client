package com.paytm.digital.education.explore.service.impl;

import com.google.common.collect.Sets;
import com.paytm.digital.education.elasticsearch.enums.DataSortOrder;
import com.paytm.digital.education.explore.database.entity.Section;
import com.paytm.digital.education.explore.database.repository.SectionRepository;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.request.dto.search.SearchRequest;
import com.paytm.digital.education.explore.response.dto.search.ExamLevelData;
import com.paytm.digital.education.explore.response.dto.search.ExamSectionData;
import com.paytm.digital.education.explore.response.dto.search.ExamSubItemData;
import com.paytm.digital.education.explore.response.dto.search.SearchResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.paytm.digital.education.elasticsearch.enums.DataSortOrder.ASC;
import static com.paytm.digital.education.explore.constants.ExploreConstants.BROWSE_BY_EXAM_LEVEL_APP;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAMS_BROWSE_BY_LEVEL_QUERY_SIZE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_GLOBAL_PRIORITY;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_LEVEL;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_STREAM_IDS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.FILTERS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.LEVEL;

@Slf4j
@Service
@AllArgsConstructor
public class SectionServiceImpl {

    private ExamLandingPageServiceImpl examSearchService;
    private SectionRepository          sectionRepository;

    public ExamLevelData getTopExamsPerLevel() {

        LinkedHashMap<String, DataSortOrder> sortOrderMap = new LinkedHashMap<>();
        sortOrderMap.put(EXAM_GLOBAL_PRIORITY, ASC);
        SearchRequest searchRequest = SearchRequest.builder()
                .fetchSearchResults(false)
                .entity(EducationEntity.EXAM)
                .sortOrder(sortOrderMap)
                .filter(new HashMap<>())
                .fetchFilter(true)
                .fetchSearchResultsPerFilter(true)
                .dataPerFilter(Arrays.asList(EXAM_LEVEL, EXAM_STREAM_IDS))
                .limit(EXAMS_BROWSE_BY_LEVEL_QUERY_SIZE)
                .build();
        try {
            SearchResponse searchResponse = examSearchService.search(searchRequest);
            return fetchTopExamsPerLevelFromSearchResponse(searchResponse);
        } catch (IOException e) {
            log.error("IO Exception for fetching top exams per level", e);
        } catch (TimeoutException e) {
            log.error("Timeout exception for fetching top top exams per level", e);
        }
        return new ExamLevelData();
    }

    private ExamLevelData fetchTopExamsPerLevelFromSearchResponse(
            SearchResponse searchResponse) {

        final List<Section> pageSections = sectionRepository.getSectionsByNameIn(
                Collections.singletonList(BROWSE_BY_EXAM_LEVEL_APP));
        ExamSectionData examSectionData =
                (ExamSectionData) searchResponse.getResults().getValues().get(0);

        if (CollectionUtils.isEmpty(pageSections) || Objects.isNull(examSectionData) || Objects
                .isNull(examSectionData.getExamsPerLevel())) {
            log.info("Section static data not present in db for section :{}, returning empty "
                    + "response", BROWSE_BY_EXAM_LEVEL_APP);
            return new ExamLevelData();
        }

        Map<String, List<ExamSubItemData>> levelGroupAndExamsMap = new HashMap<>();
        Section section = pageSections.get(0);

        ExamLevelData examLevelData = new ExamLevelData();
        for (Map<String, Object> item : section.getItems()) {
            List<ExamSubItemData> examsInLevelGroupList = new ArrayList<>();

            for (Map.Entry<String, Object> levelGroupEntry : item.entrySet()) {
                Map<String, Object> levelGroupValue =
                        (Map<String, Object>) levelGroupEntry.getValue();
                List<Map<String, Object>> filters =
                        (List<Map<String, Object>>) levelGroupValue.get(FILTERS);
                updateExamsInLevelGroup(examSectionData.getExamsPerLevel(), examsInLevelGroupList,
                        filters);

                if (!CollectionUtils.isEmpty(examsInLevelGroupList)) {
                    levelGroupAndExamsMap.put(levelGroupEntry.getKey(), examsInLevelGroupList);
                }
            }
        }
        examLevelData.setExams(levelGroupAndExamsMap);
        return examLevelData;
    }

    private void updateExamsInLevelGroup(Map<String, List<ExamSubItemData>> levelAndExamsMap,
            List<ExamSubItemData> examsInLevelGroupList, List<Map<String, Object>> filters) {
        if (!CollectionUtils.isEmpty(filters)) {
            List<String> examLevelsInGroup = (List<String>) filters.get(0).get(LEVEL);
            examsInLevelGroupList.addAll(
                    Sets.intersection(levelAndExamsMap.keySet(), Sets.newHashSet(examLevelsInGroup))
                            .stream()
                            .flatMap(key -> levelAndExamsMap.get(key).stream())
                            .collect(Collectors.toMap(ExamSubItemData::getExamId,
                                    Function.identity(), (exam1, exam2) -> exam1,
                                    LinkedHashMap::new))
                            .values());
        }
    }
}
