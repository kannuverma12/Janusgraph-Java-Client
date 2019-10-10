package com.paytm.digital.education.coaching.consumer.service.search;

import com.paytm.digital.education.coaching.consumer.model.request.SearchRequest;
import com.paytm.digital.education.coaching.consumer.model.response.search.CoachingCourseData;
import com.paytm.digital.education.coaching.consumer.model.response.search.CoachingCoursesTopHitsData;
import com.paytm.digital.education.coaching.consumer.model.response.search.SearchBaseData;
import com.paytm.digital.education.coaching.consumer.model.response.search.SearchResponse;
import com.paytm.digital.education.coaching.consumer.model.response.search.SearchResult;
import com.paytm.digital.education.coaching.consumer.service.search.helper.CoachingSearchAggregateHelper;
import com.paytm.digital.education.coaching.enums.CoachingCourseType;
import com.paytm.digital.education.coaching.es.model.CoachingCourseSearch;
import com.paytm.digital.education.coaching.es.model.CoachingInstituteSearch;
import com.paytm.digital.education.coaching.utils.SearchUtils;
import com.paytm.digital.education.elasticsearch.models.AggregateField;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.elasticsearch.models.TopHitsAggregationResponse;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.enums.es.FilterQueryType;
import com.paytm.digital.education.utility.CommonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_COMPONENT;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_COURSE_DURATION;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_COURSE_EXAMS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_COURSE_INSTITUTE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_COURSE_LEVEL;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_COURSE_STREAMS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COURSE_FILTER_NAMESPACE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COURSE_SEARCH_NAMESPACE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COURSE_TYPE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.IS_DYNAMIC;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.IS_ENABLED;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.STREAM_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.COACHING_COURSE_NAME;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.COACHING_COURSE_NAME_BOOST;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.COACHING_INSTITUTE_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.EXAM_IDS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.SEARCH_ANALYZER_COACHING_COURSE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.SEARCH_INDEX_COACHING_COURSE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.STREAM_IDS;
import static com.paytm.digital.education.enums.es.FilterQueryType.TERMS;

@Slf4j
@Service
@AllArgsConstructor
public class CoachingCourseSearchService extends AbstractSearchService {

    private static Map<String, Float>            searchFieldKeys;
    private static Map<String, FilterQueryType>  filterQueryTypeMap;
    private        CoachingSearchAggregateHelper coachingSearchAggregateHelper;

    @PostConstruct
    public void init() {
        filterQueryTypeMap = new HashMap<>();
        filterQueryTypeMap.put(STREAM_ID, TERMS);
        filterQueryTypeMap.put(STREAM_IDS, TERMS);
        filterQueryTypeMap.put(EXAM_ID, TERMS);
        filterQueryTypeMap.put(EXAM_IDS, TERMS);
        filterQueryTypeMap.put(COACHING_INSTITUTE_ID, TERMS);
        filterQueryTypeMap.put(COACHING_COURSE_STREAMS, TERMS);
        filterQueryTypeMap.put(COACHING_COURSE_EXAMS, TERMS);
        filterQueryTypeMap.put(COURSE_TYPE, TERMS);
        filterQueryTypeMap.put(COACHING_COURSE_INSTITUTE, TERMS);
        filterQueryTypeMap.put(COACHING_COURSE_LEVEL, TERMS);
        filterQueryTypeMap.put(COACHING_COURSE_DURATION, TERMS);
        filterQueryTypeMap.put(IS_ENABLED, TERMS);
        filterQueryTypeMap.put(IS_DYNAMIC, TERMS);
        searchFieldKeys = new HashMap<>();
        searchFieldKeys.put(COACHING_COURSE_NAME, COACHING_COURSE_NAME_BOOST);
    }

    @Override
    @Cacheable(value = "coaching_course_search")
    public SearchResponse search(SearchRequest searchRequest) {
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
        if (searchRequest.isFetchSearchResultsPerFilter()) {
            populateSearchResultPerLevel(searchResponse, elasticResponse);
        }

        buildSearchResponse(searchResponse, elasticResponse, elasticRequest, COACHING_COMPONENT,
                COURSE_FILTER_NAMESPACE, COURSE_SEARCH_NAMESPACE);
        return searchResponse;
    }

    @Override
    protected ElasticRequest buildSearchRequest(SearchRequest searchRequest) {
        ElasticRequest elasticRequest = createSearchRequest(searchRequest,
                SEARCH_ANALYZER_COACHING_COURSE, SEARCH_INDEX_COACHING_COURSE);
        populateSearchFields(searchRequest, elasticRequest, searchFieldKeys,
                CoachingCourseSearch.class);

        Map<String, List<Object>> filters = searchRequest.getFilter();
        if (CollectionUtils.isEmpty(filters)) {
            filters = new HashMap<>();
        }
        filters.put(IS_ENABLED, Collections.singletonList(true));
        filters.put(IS_DYNAMIC, Collections.singletonList(false));
        searchRequest.setFilter(filters);

        populateFilterFields(searchRequest, elasticRequest, CoachingCourseSearch.class,
                filterQueryTypeMap);

        AggregateField[] aggregateFields = searchRequest.isFetchSearchResultsPerFilter()
                ? coachingSearchAggregateHelper
                .getTopHitsAggregateData(searchRequest.getDataPerFilter())
                : coachingSearchAggregateHelper.getCoachingCourseAggregateData();
        if (searchRequest.getFetchFilter()) {
            populateAggregateFields(searchRequest, elasticRequest,
                    aggregateFields, CoachingInstituteSearch.class);
        }
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
            ElasticResponse elasticResponse, Map<String, Map<String, Object>> properties,
            ElasticRequest elasticRequest) {
        List<CoachingCourseSearch> coachingCourseSearches = elasticResponse.getDocuments();
        SearchResult searchResults = new SearchResult();
        if (!CollectionUtils.isEmpty(coachingCourseSearches)) {
            searchResults.setEntity(EducationEntity.COACHING_COURSE);
            List<SearchBaseData> courseDataList = new ArrayList<>();

            for (CoachingCourseSearch coachingCourseSearch : coachingCourseSearches) {
                courseDataList.add(this.toCoachingCourseData(coachingCourseSearch));
            }
            searchResults.setValues(courseDataList);
        }
        searchResponse.setResults(searchResults);
    }

    private void populateSearchResultPerLevel(SearchResponse searchResponse,
            ElasticResponse elasticResponse) {
        SearchResult searchResults = new SearchResult();
        if (elasticResponse.getAggregationResponse().containsKey(STREAM_IDS)) {
            TopHitsAggregationResponse<CoachingCourseSearch> topHitsAggregationResponse =
                    (TopHitsAggregationResponse<CoachingCourseSearch>) elasticResponse
                            .getAggregationResponse().get(STREAM_IDS);
            Map<String, List<CoachingCourseData>> coursesPerStream = new HashMap<>();
            topHitsAggregationResponse.getDocumentsPerEntity().forEach((key, documents) -> {
                List<CoachingCourseData> coursesDataList = new ArrayList<>();
                documents.forEach(courseSearch -> {
                    coursesDataList.add(toCoachingCourseData(courseSearch));
                });
                coursesPerStream.put(key.getKey(), coursesDataList);
            });
            CoachingCoursesTopHitsData examsTopHitsData = CoachingCoursesTopHitsData.builder()
                    .coursesPerStream(coursesPerStream).build();

            List<SearchBaseData> values = new ArrayList<>();
            values.add(examsTopHitsData);
            searchResults.setValues(values);
        }
        searchResponse.setResults(searchResults);
    }

    private CoachingCourseData toCoachingCourseData(CoachingCourseSearch coachingCourseSearch) {

        CoachingCourseData toAdd = CoachingCourseData
                .builder()
                .courseId(coachingCourseSearch.getCourseId())
                .courseName(coachingCourseSearch.getCourseName())
                .coachingInstituteId(coachingCourseSearch.getCoachingInstituteId())
                .coachingInstituteName(coachingCourseSearch.getCoachingInstituteName())
                .discountedPrice(coachingCourseSearch.getDiscountedPrice())
                .originalPrice(coachingCourseSearch.getOriginalPrice())
                .eligibility(coachingCourseSearch.getEligibility())
                .courseDurationDays(coachingCourseSearch.getCourseDurationDays())
                .duration(coachingCourseSearch.getDuration())
                .urlDisplayKey(CommonUtil
                        .convertNameToUrlDisplayName(coachingCourseSearch.getCourseName()))
                .logo(CoachingCourseType.getStaticDataByCourseType(
                        coachingCourseSearch.getCourseType()).getImageUrl())
                .targetExam(!CollectionUtils.isEmpty(coachingCourseSearch.getExamNames())
                        ? coachingCourseSearch.getExamNames().get(0) : null)
                .build();

        if (Objects.nonNull(coachingCourseSearch.getLevel())) {
            toAdd.setCourseLevel(coachingCourseSearch.getLevel().getDisplayName());
        }

        if (Objects.nonNull(coachingCourseSearch.getCourseType())) {
            toAdd.setCourseType(coachingCourseSearch.getCourseType().getText());
        }

        if (Objects.nonNull(coachingCourseSearch.getDurationType())) {
            toAdd.setDurationType(coachingCourseSearch.getDurationType().getText());
        }

        return toAdd;
    }
}
