package com.paytm.digital.education.coaching.consumer.service;

import com.paytm.digital.education.coaching.consumer.model.request.SearchRequest;
import com.paytm.digital.education.coaching.consumer.model.response.search.CoachingInstituteData;
import com.paytm.digital.education.coaching.consumer.model.response.search.SearchBaseData;
import com.paytm.digital.education.coaching.consumer.model.response.search.SearchResponse;
import com.paytm.digital.education.coaching.consumer.model.response.search.SearchResult;
import com.paytm.digital.education.coaching.consumer.service.helper.CoachingSearchAggregateHelper;
import com.paytm.digital.education.coaching.es.model.CoachingInstituteSearch;
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

import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.STREAM_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.COACHING_INSTITUTE_BRAND;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.COACHING_INSTITUTE_BRAND_BOOST;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.EXAM_IDS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.SEARCH_ANALYZER_COACHING_INSTITUTE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.SEARCH_INDEX_COACHING_INSTITUTE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.STREAM_IDS;
import static com.paytm.digital.education.elasticsearch.enums.FilterQueryType.TERMS;

@Slf4j
@Service
@AllArgsConstructor
public class CoachingInstituteSearchService extends AbstractSearchService {

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
        searchFieldKeys = new HashMap<>();
        searchFieldKeys.put(COACHING_INSTITUTE_BRAND, COACHING_INSTITUTE_BRAND_BOOST);
    }

    @Override
    @Cacheable(value = "coaching_institute_search")
    public SearchResponse search(SearchRequest searchRequest) throws IOException, TimeoutException {
        validateRequest(searchRequest, filterQueryTypeMap);
        ElasticRequest elasticRequest = buildSearchRequest(searchRequest);
        ElasticResponse elasticResponse =
                initiateSearch(elasticRequest, CoachingInstituteSearch.class);
        SearchResponse searchResponse = new SearchResponse(searchRequest.getTerm());
        buildSearchResponse(searchResponse, elasticResponse, elasticRequest);
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
            ElasticResponse elasticResponse, Map<String, Map<String, Object>> properties) {
        List<CoachingInstituteSearch> coachingInstituteSearches = elasticResponse.getDocuments();
        SearchResult searchResults = new SearchResult();
        if (!CollectionUtils.isEmpty(coachingInstituteSearches)) {
            searchResults.setEntity(EducationEntity.COACHING_INSTITUTE);
            List<SearchBaseData> instituteDataList = new ArrayList<>();

            for (CoachingInstituteSearch coachingInstituteSearch : coachingInstituteSearches) {
                instituteDataList.add(CoachingInstituteData
                        .builder()
                        .coachingInstituteId(coachingInstituteSearch.getCoachingInstituteId())
                        .urlDisplayKey(CommonUtil.convertNameToUrlDisplayName(
                                coachingInstituteSearch.getBrandName()))
                        .brandName(coachingInstituteSearch.getBrandName())
                        .logo(coachingInstituteSearch.getLogo())
                        .build());
            }
            searchResults.setValues(instituteDataList);
        }
        searchResponse.setResults(searchResults);
    }
}
