package com.paytm.digital.education.coaching.consumer.service.search;

import com.paytm.digital.education.coaching.consumer.model.request.SearchRequest;
import com.paytm.digital.education.coaching.consumer.model.response.search.CoachingInstituteData;
import com.paytm.digital.education.coaching.consumer.model.response.search.SearchBaseData;
import com.paytm.digital.education.coaching.consumer.model.response.search.SearchResponse;
import com.paytm.digital.education.coaching.consumer.model.response.search.SearchResult;
import com.paytm.digital.education.coaching.consumer.service.search.helper.CoachingSearchAggregateHelper;
import com.paytm.digital.education.coaching.es.model.CoachingInstituteSearch;
import com.paytm.digital.education.coaching.utils.ImageUtils;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_COMPONENT;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_INSTITUTE_COURSE_TYPES;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_INSTITUTE_EXAMS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_INSTITUTE_STREAMS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.INSTITUTE_FILTER_NAMESPACE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.INSTITUTE_PLACEHOLDER;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.INSTITUTE_SEARCH_NAMESPACE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.STREAM_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.COACHING_INSTITUTE_BRAND;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.COACHING_INSTITUTE_BRAND_BOOST;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.EXAM_IDS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.SEARCH_ANALYZER_COACHING_INSTITUTE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.SEARCH_INDEX_COACHING_INSTITUTE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.STREAM_IDS;
import static com.paytm.digital.education.constant.CommonConstants.TOP_COACHING_INSTITUTES_LOGO;
import static com.paytm.digital.education.elasticsearch.enums.FilterQueryType.TERMS;

@Slf4j
@Service
@AllArgsConstructor
public class CoachingInstituteSearchService extends AbstractSearchService {

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
        filterQueryTypeMap.put(COACHING_INSTITUTE_STREAMS, TERMS);
        filterQueryTypeMap.put(COACHING_INSTITUTE_EXAMS, TERMS);
        filterQueryTypeMap.put(COACHING_INSTITUTE_COURSE_TYPES, TERMS);
        searchFieldKeys = new HashMap<>();
        searchFieldKeys.put(COACHING_INSTITUTE_BRAND, COACHING_INSTITUTE_BRAND_BOOST);
    }

    @Override
    @Cacheable(value = "coaching_institute_search")
    public SearchResponse search(SearchRequest searchRequest) {
        validateRequest(searchRequest, filterQueryTypeMap);
        ElasticRequest elasticRequest = buildSearchRequest(searchRequest);
        ElasticResponse elasticResponse;
        try {
            elasticResponse = initiateSearch(elasticRequest, CoachingInstituteSearch.class);
        } catch (Exception e) {
            log.error("Error encountered in search query for coaching institute ", e);
            elasticResponse = new ElasticResponse();
        }
        SearchResponse searchResponse = new SearchResponse(searchRequest.getTerm());
        buildSearchResponse(searchResponse, elasticResponse, elasticRequest,COACHING_COMPONENT,
                INSTITUTE_FILTER_NAMESPACE,INSTITUTE_SEARCH_NAMESPACE);
        return searchResponse;
    }

    @Override
    protected ElasticRequest buildSearchRequest(SearchRequest searchRequest) {
        ElasticRequest elasticRequest = createSearchRequest(searchRequest,
                SEARCH_ANALYZER_COACHING_INSTITUTE, SEARCH_INDEX_COACHING_INSTITUTE);
        populateSearchFields(searchRequest, elasticRequest, searchFieldKeys,
                CoachingInstituteSearch.class);
        populateFilterFields(searchRequest, elasticRequest, CoachingInstituteSearch.class,
                filterQueryTypeMap);
        if (searchRequest.getFetchFilter()) {
            populateAggregateFields(searchRequest, elasticRequest,
                    coachingSearchAggregateHelper.getCoachingInstituteAggregateData(),
                    CoachingInstituteSearch.class);
        }
        if (StringUtils.isBlank(searchRequest.getTerm())) {
            SearchUtils.setSortKeysInOrder(searchRequest);
        } else {
            searchRequest.setSortOrder(null);
        }

        populateSortFields(searchRequest, elasticRequest, CoachingInstituteSearch.class);
        return elasticRequest;
    }

    @Override
    protected void populateSearchResults(SearchResponse searchResponse,
            ElasticResponse elasticResponse, Map<String, Map<String, Object>> properties,
            ElasticRequest elasticRequest) {
        List<CoachingInstituteSearch> coachingInstituteSearches = elasticResponse.getDocuments();
        SearchResult searchResults = new SearchResult();
        if (!CollectionUtils.isEmpty(coachingInstituteSearches)) {
            searchResults.setEntity(EducationEntity.COACHING_INSTITUTE);
            List<SearchBaseData> instituteDataList = new ArrayList<>();
            for (CoachingInstituteSearch coachingInstituteSearch : coachingInstituteSearches) {
                CoachingInstituteData toAdd = CoachingInstituteData
                        .builder()
                        .coachingInstituteId(coachingInstituteSearch.getCoachingInstituteId())
                        .urlDisplayKey(CommonUtil.convertNameToUrlDisplayName(
                                coachingInstituteSearch.getBrandName()))
                        .brandName(coachingInstituteSearch.getBrandName())
                        .logo(ImageUtils.getImageWithAbsolutePath(coachingInstituteSearch.getLogo(),
                                INSTITUTE_PLACEHOLDER, TOP_COACHING_INSTITUTES_LOGO))
                        .build();

                instituteDataList.add(toAdd);
            }
            searchResults.setValues(instituteDataList);
        }
        searchResponse.setResults(searchResults);
    }
}
