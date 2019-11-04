package com.paytm.digital.education.coaching.consumer.service.search.helper;

import com.paytm.digital.education.coaching.consumer.model.request.SearchRequest;
import com.paytm.digital.education.coaching.consumer.model.response.search.SearchBaseData;
import com.paytm.digital.education.coaching.consumer.model.response.search.SearchResponse;
import com.paytm.digital.education.coaching.consumer.service.search.AbstractSearchService;
import com.paytm.digital.education.coaching.consumer.service.search.CoachingCourseSearchService;
import com.paytm.digital.education.coaching.consumer.service.search.CoachingInstituteSearchService;
import com.paytm.digital.education.coaching.consumer.service.search.ExamSearchService;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.enums.es.DataSortOrder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.TOP_ELEMENTS_ANY_PAGE_LIMIT;

@Service
@AllArgsConstructor
@Slf4j
public class SearchDataHelper {

    private CoachingCourseSearchService    coachingCourseSearch;
    private CoachingInstituteSearchService coachingInstituteSearchService;
    private ExamSearchService              examSearchService;

    public List<SearchBaseData> getTopSearchData(Map<String, List<Object>> filters,
            EducationEntity entity, LinkedHashMap<String, DataSortOrder> sortOrderMap) {
        SearchRequest searchRequest = SearchRequest
                .builder()
                .fetchFilter(false)
                .entity(entity)
                .fetchSearchResults(true)
                .offset(0)
                .limit(TOP_ELEMENTS_ANY_PAGE_LIMIT)
                .filter(filters)
                .sortOrder(sortOrderMap)
                .build();

        try {
            SearchResponse searchResponse = handler(entity).search(searchRequest);
            return searchResponse.getResults().getValues();
        } catch (IOException e) {
            log.error("IO Exception for fetching top {} : {} for : {}",
                    entity.toString(), e.getMessage(), filters.toString());
        } catch (TimeoutException e) {
            log.error("Timeout exception for fetching top {} : {} for : {}",
                    entity.toString(), e.getMessage(), filters.toString());
        }
        return null;
    }

    private AbstractSearchService handler(EducationEntity educationEntity) {
        switch (educationEntity) {
            case COACHING_COURSE:
                return coachingCourseSearch;
            case COACHING_INSTITUTE:
                return coachingInstituteSearchService;
            case EXAM:
                return examSearchService;
            default:
                throw new RuntimeException("Invalid entity requested.");
        }
    }


}
