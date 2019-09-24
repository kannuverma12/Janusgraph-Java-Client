package com.paytm.digital.education.coaching.consumer.service;

import com.paytm.digital.education.coaching.consumer.model.request.SearchRequest;
import com.paytm.digital.education.coaching.consumer.model.response.search.CoachingInstituteData;
import com.paytm.digital.education.coaching.consumer.model.response.search.ExamData;
import com.paytm.digital.education.coaching.consumer.model.response.search.ExamsTopHitsData;
import com.paytm.digital.education.coaching.consumer.model.response.search.SearchResponse;
import com.paytm.digital.education.coaching.consumer.service.helper.SearchDataHelper;
import com.paytm.digital.education.database.entity.Section;
import com.paytm.digital.education.elasticsearch.enums.DataSortOrder;
import com.paytm.digital.education.enums.EducationEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.LandingPage.FULL_NAME;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.LandingPage.ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.LandingPage.LOGO;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.LandingPage.NAME;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.LandingPage.URL_DISPLAY_KEY;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.IGNORE_ENTITY_POSITION;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.STREAM_IDS;
import static com.paytm.digital.education.constant.CommonConstants.COACHING_STREAMS;
import static com.paytm.digital.education.constant.CommonConstants.COACHING_TOP_EXAMS;
import static com.paytm.digital.education.constant.CommonConstants.TOP_COACHING_INSTITUTES_LOGO;
import static com.paytm.digital.education.elasticsearch.enums.DataSortOrder.ASC;
import static com.paytm.digital.education.enums.EducationEntity.EXAM;

@Service
@AllArgsConstructor
@Slf4j
public class LandingPageService {

    private SearchDataHelper  searchDataHelper;
    private ExamSearchService examSearchService;

    public void addDynamicData(List<Section> sections) {
        for (Section section : sections) {
            switch (section.getType()) {
                case TOP_COACHING_INSTITUTES_LOGO:
                    addTopCoachingInstitutes(section);
                    break;
                case COACHING_TOP_EXAMS:
                    addTopExams(section, sections);
                    break;
                default:
            }
        }
    }

    private void addTopCoachingInstitutes(Section section) {
        LinkedHashMap<String, DataSortOrder> sortOrder = new LinkedHashMap<>();
        sortOrder.put(IGNORE_ENTITY_POSITION, ASC);
        List<CoachingInstituteData> institutes =
                (List<CoachingInstituteData>) (List<?>) searchDataHelper
                        .getTopSearchData(null, EducationEntity.COACHING_INSTITUTE, sortOrder);
        List<Map<String, Object>> itemList = new ArrayList<>();
        for (CoachingInstituteData coachingInstitute : institutes) {
            itemList.add(
                    getItem(coachingInstitute.getBrandName(), coachingInstitute.getUrlDisplayKey(),
                            coachingInstitute.getCoachingInstituteId(), coachingInstitute.getLogo(),
                            null));
        }
        section.setItems(itemList);
    }

    private void addTopExams(Section section, List<Section> sections) {
        List<ExamData> exams = getTopExamsPerStream(sections);
        List<Map<String, Object>> itemList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(exams)) {
            for (ExamData exam : exams) {
                itemList.add(
                        getItem(exam.getExamShortName(), exam.getUrlDisplayKey(), exam.getExamId(),
                                exam.getLogoUrl(), exam.getOfficialName()));
            }
            section.setItems(itemList);
        }
    }

    private List<ExamData> getTopExamsPerStream(List<Section> sections) {
        LinkedHashMap<String, DataSortOrder> sortOrderMap = new LinkedHashMap<>();
        sortOrderMap.put(IGNORE_ENTITY_POSITION, ASC);
        SearchRequest searchRequest = SearchRequest.builder()
                .fetchSearchResults(false)
                .entity(EXAM)
                .sortOrder(sortOrderMap)
                .dataPerFilter(STREAM_IDS)
                .fetchFilter(true)
                .fetchSearchResultsPerFilter(true)
                .limit(1)
                .build();
        try {
            SearchResponse searchResponse = examSearchService.search(searchRequest);
            return fetchTopExamsPerStreamFromSearchResponse(searchResponse, sections);
        } catch (IOException e) {
            log.error("IO Exception for fetching top exams per stream "
                    + "", e);
        } catch (TimeoutException e) {
            log.error("Timeout exception for fetching top top exams per stream ",
                    e);
        }
        return null;
    }

    private List<String> getStreamsInOrder(List<Section> sections) {
        List<String> streams = new ArrayList<>();
        for (Section section : sections) {
            if (COACHING_STREAMS.equals(section.getType())) {
                for (Map<String, Object> item : section.getItems()) {
                    streams.add(item.get(ID).toString());
                }
                break;
            }
        }
        return streams;
    }

    private List<ExamData> fetchTopExamsPerStreamFromSearchResponse(SearchResponse searchResponse,
            List<Section> sections) {
        Map<String, List<ExamData>> examsPerStream =
                ((ExamsTopHitsData) searchResponse.getResults().getValues().get(0))
                        .getExamsPerStream();
        List<String> streamsInOrder = getStreamsInOrder(sections);
        List<ExamData> exams = new ArrayList<>();
        Set<Integer> examIds = new HashSet<>();
        for (String stream : streamsInOrder) {
            List<ExamData> examData = examsPerStream.get(stream);
            if (!CollectionUtils.isEmpty(examData)) {
                for (ExamData exam : examData) {
                    if (examIds.add(exam.getExamId())) {
                        exams.add(exam);
                        break;
                    }
                }
            }
        }
        return exams;
    }

    private Map<String, Object> getItem(String name, String urlDisplayKey, Object id, String logo,
            String fullName) {
        Map<String, Object> item = new HashMap<>();
        item.put(NAME, name);
        item.put(ID, id);
        item.put(URL_DISPLAY_KEY, urlDisplayKey);
        item.put(LOGO, logo);
        if (StringUtils.isNotBlank(fullName)) {
            item.put(FULL_NAME, fullName);
        }
        return item;
    }

}
