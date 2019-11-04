package com.paytm.digital.education.coaching.consumer.service.search;

import com.paytm.digital.education.coaching.consumer.model.request.SearchRequest;
import com.paytm.digital.education.coaching.consumer.model.response.search.SearchResponse;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class SearchService {

    private CoachingCourseSearchService    coachingCourseSearchService;
    private CoachingInstituteSearchService coachingInstituteSearchService;
    private ExamSearchService              examSearchService;
    private CoachingCenterSearchService    coachingCenterSearchService;

    public SearchResponse search(SearchRequest searchRequest, Long userId) throws Exception {
        long startTime = System.currentTimeMillis();
        log.debug("Starting search at : " + startTime);
        SearchResponse response = handler(searchRequest.getEntity()).search(searchRequest);
        log.debug("Search Response : {}", JsonUtils.toJson(response));
        log.debug("Time taken in search : " + (System.currentTimeMillis() - startTime));
        return response;
    }

    private AbstractSearchService handler(EducationEntity educationEntity) {
        switch (educationEntity) {
            case COACHING_COURSE:
                return coachingCourseSearchService;
            case COACHING_INSTITUTE:
                return coachingInstituteSearchService;
            case EXAM:
                return examSearchService;
            case COACHING_CENTER:
                return coachingCenterSearchService;
            default:
                throw new RuntimeException("Invalid entity requested.");
        }
    }

}
