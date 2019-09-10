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
import com.paytm.digital.education.utility.CommonUtil;
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

import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_PLACEHOLDER;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.INSTITUTE_PLACEHOLDER;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.LandingPage.FULL_NAME;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.LandingPage.ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.LandingPage.LOGO;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.LandingPage.NAME;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.LandingPage.URL_DISPLAY_KEY;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.IGNORE_ENTITY_POSITION;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.STREAM_IDS;
import static com.paytm.digital.education.constant.CommonConstants.COACHING_TOP_EXAMS;
import static com.paytm.digital.education.constant.CommonConstants.TOP_COACHING_INSTITUTES;
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
                case TOP_COACHING_INSTITUTES:
                    addTopCoachingInstitutes(section);
                    break;
                case COACHING_TOP_EXAMS:
                    addTopExams(section);
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
            String logoUrl = null;
            if (StringUtils.isNotBlank(coachingInstitute.getLogo())) {
                logoUrl = CommonUtil
                        .getAbsoluteUrl(coachingInstitute.getLogo(), TOP_COACHING_INSTITUTES);
            } else {
                logoUrl = CommonUtil.getAbsoluteUrl(INSTITUTE_PLACEHOLDER, TOP_COACHING_INSTITUTES);
            }
            itemList.add(
                    getItem(coachingInstitute.getBrandName(), coachingInstitute.getUrlDisplayKey(),
                            coachingInstitute.getCoachingInstituteId(), logoUrl, null));
        }
        section.setItems(itemList);
    }

    private void addTopExams(Section section) {
        List<ExamData> exams = getTopExamsPerStream();
        List<Map<String, Object>> itemList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(exams)) {
            for (ExamData exam : exams) {
                String logoUrl = null;
                if (StringUtils.isNotBlank(exam.getLogoUrl())) {
                    logoUrl = CommonUtil.getAbsoluteUrl(exam.getLogoUrl(), COACHING_TOP_EXAMS);
                } else {
                    logoUrl = CommonUtil.getAbsoluteUrl(EXAM_PLACEHOLDER, COACHING_TOP_EXAMS);
                }
                itemList.add(
                        getItem(exam.getExamShortName(), exam.getUrlDisplayKey(), exam.getExamId(),
                                logoUrl, exam.getOfficialName()));
            }
            section.setItems(itemList);
        }
    }

    private List<ExamData> getTopExamsPerStream() {
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
            return fetchTopExamsPerStreamFromSearchResponse(searchResponse);
        } catch (IOException e) {
            log.error("IO Exception for fetching top exams per stream "
                    + "", e);
        } catch (TimeoutException e) {
            log.error("Timeout exception for fetching top top exams per stream ",
                    e);
        }
        return null;
    }

    private List<ExamData> fetchTopExamsPerStreamFromSearchResponse(SearchResponse searchResponse) {
        Map<String, List<ExamData>> examsPerStream =
                ((ExamsTopHitsData) searchResponse.getResults().getValues().get(0))
                        .getExamsPerStream();
        List<ExamData> exams = new ArrayList<>();
        Set<Integer> examIds = new HashSet<>();
        for (Map.Entry<String, List<ExamData>> entry : examsPerStream.entrySet()) {
            for (ExamData examData : entry.getValue()) {
                if (examIds.add(examData.getExamId())) {
                    exams.add(examData);
                    break;
                }
            }
        }
        return new ArrayList<>(exams);
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
