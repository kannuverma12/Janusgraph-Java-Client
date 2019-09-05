package com.paytm.digital.education.coaching.consumer.service;

import com.paytm.digital.education.coaching.consumer.model.request.SearchRequest;
import com.paytm.digital.education.coaching.consumer.model.response.search.CoachingCourseData;
import com.paytm.digital.education.coaching.consumer.model.response.search.SearchBaseData;
import com.paytm.digital.education.coaching.consumer.model.response.search.SearchResponse;
import com.paytm.digital.education.coaching.consumer.model.response.search.SearchResult;
import com.paytm.digital.education.coaching.consumer.service.helper.CoachingSearchAggregateHelper;
import com.paytm.digital.education.coaching.es.model.CoachingCourseSearch;
import com.paytm.digital.education.coaching.utils.SearchUtils;
import com.paytm.digital.education.elasticsearch.enums.FilterQueryType;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.utility.CommonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_COURSE_PLACEHOLDER;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.STREAM_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.COACHING_COURSE_NAME;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.COACHING_COURSE_NAME_BOOST;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.COACHING_INSTITUTE_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.EXAM_IDS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.SEARCH_ANALYZER_COACHING_COURSE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.SEARCH_INDEX_COACHING_COURSE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.STREAM_IDS;
import static com.paytm.digital.education.constant.CommonConstants.COACHING_COURSES;
import static com.paytm.digital.education.elasticsearch.enums.FilterQueryType.TERMS;

@Slf4j
@Service
@AllArgsConstructor
public class CoachingCourseSearchService extends AbstractSearchService {

    private        CoachingSearchAggregateHelper coachingSearchAggregateHelper;
    private static Map<String, Float>            searchFieldKeys;
    private static Map<String, FilterQueryType>  filterQueryTypeMap;

    @PostConstruct
    public void init() {
        filterQueryTypeMap = new HashMap<>();
        filterQueryTypeMap.put(STREAM_ID, TERMS);
        filterQueryTypeMap.put(STREAM_IDS, TERMS);
        filterQueryTypeMap.put(EXAM_ID, TERMS);
        filterQueryTypeMap.put(EXAM_IDS, TERMS);
        filterQueryTypeMap.put(COACHING_INSTITUTE_ID, TERMS);
        searchFieldKeys = new HashMap<>();
        searchFieldKeys.put(COACHING_COURSE_NAME, COACHING_COURSE_NAME_BOOST);
    }

    @Override
    @Cacheable(value = "coaching_course_search")
    public SearchResponse search(SearchRequest searchRequest) throws IOException, TimeoutException {
        validateRequest(searchRequest, filterQueryTypeMap);
        ElasticRequest elasticRequest = buildSearchRequest(searchRequest);
        ElasticResponse elasticResponse;
        try {
            elasticResponse = initiateSearch(elasticRequest, CoachingCourseSearch.class);
        } catch (Exception e) {
            log.error("Error encountered in search query for coaching course ", e);
            elasticResponse = new ElasticResponse();
        }
        SearchResponse searchResponse = new SearchResponse(searchRequest.getTerm());
        buildSearchResponse(searchResponse, elasticResponse, elasticRequest);
        return searchResponse;
    }

    @Override
    protected ElasticRequest buildSearchRequest(SearchRequest searchRequest) {
        ElasticRequest elasticRequest = createSearchRequest(searchRequest,
                SEARCH_ANALYZER_COACHING_COURSE, SEARCH_INDEX_COACHING_COURSE);
        populateSearchFields(searchRequest, elasticRequest, searchFieldKeys,
                CoachingCourseSearch.class);
        populateFilterFields(searchRequest, elasticRequest, CoachingCourseSearch.class,
                filterQueryTypeMap);

        if (StringUtils.isBlank(searchRequest.getTerm())) {
            SearchUtils.setSortKeysInOrder(searchRequest);
        } else {
            searchRequest.setSortOrder(null);
        }

        populateSortFields(searchRequest, elasticRequest, CoachingCourseSearch.class);
        return elasticRequest;
    }

    @Override
    protected void populateSearchResults(SearchResponse searchResponse,
            ElasticResponse elasticResponse, Map<String, Map<String, Object>> properties) {
        List<CoachingCourseSearch> coachingCourseSearches = elasticResponse.getDocuments();
        SearchResult searchResults = new SearchResult();
        if (!CollectionUtils.isEmpty(coachingCourseSearches)) {
            searchResults.setEntity(EducationEntity.COACHING_COURSE);
            List<SearchBaseData> courseDataList = new ArrayList<>();

            for (CoachingCourseSearch coachingCourseSearch : coachingCourseSearches) {
                CoachingCourseData toAdd = CoachingCourseData
                        .builder()
                        .courseId(coachingCourseSearch.getCourseId())
                        .courseName(coachingCourseSearch.getCourseName())
                        .coachingInstituteId(coachingCourseSearch.getCoachingInstituteId())
                        .coachingInstituteName(coachingCourseSearch.getCoachingInstituteName())
                        .courseType(coachingCourseSearch.getCourseType())
                        .courseDurationDays(coachingCourseSearch.getCourseDurationDays())
                        .price(coachingCourseSearch.getPrice())
                        .currency(coachingCourseSearch.getCurrency())
                        .level(coachingCourseSearch.getLevel())
                        .eligibility(coachingCourseSearch.getEligibility())
                        .urlDisplayKey(CommonUtil
                                .convertNameToUrlDisplayName(coachingCourseSearch.getCourseName()))
                        .build();

                if (!StringUtils.isBlank(coachingCourseSearch.getLogo())) {
                    toAdd.setLogo(CommonUtil.getAbsoluteUrl(coachingCourseSearch.getLogo(),
                            COACHING_COURSES));
                } else {
                    toAdd.setLogo(CommonUtil.getAbsoluteUrl(COACHING_COURSE_PLACEHOLDER,
                            COACHING_COURSES));
                }

                courseDataList.add(toAdd);
            }
            searchResults.setValues(courseDataList);
        }
        searchResponse.setResults(searchResults);
    }
}
