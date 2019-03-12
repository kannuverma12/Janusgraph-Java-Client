package com.paytm.digital.education.explore.service.impl;

import static com.paytm.digital.education.explore.constants.ExploreConstants.DEFAULT_SIZE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SEARCH_ANALYZER_INSTITUTE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SEARCH_INDEX_INSTITUTE;

import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.explore.es.model.CourseSearch;
import com.paytm.digital.education.explore.es.model.ExamSearch;
import com.paytm.digital.education.explore.es.model.InstituteSearch;
import com.paytm.digital.education.explore.request.dto.search.SearchRequest;
import com.paytm.digital.education.explore.response.dto.search.SearchResponse;
import com.paytm.digital.education.search.service.ISearchService;
import com.paytm.digital.education.utility.HierarchyIdentifierUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import javax.annotation.PostConstruct;

@Slf4j
@Component
public abstract class AbstractSearchServiceImpl {

    @Autowired
    private   ISearchService                  searchService;
    protected Map<Class, Map<String, String>> hierarchyMap;

    @PostConstruct
    private void generateLevelMap() {
        hierarchyMap = new HashMap<>();
        hierarchyMap.put(InstituteSearch.class, HierarchyIdentifierUtils.getClassHierarchy(
                InstituteSearch.class));
        hierarchyMap.put(ExamSearch.class,
                HierarchyIdentifierUtils.getClassHierarchy(ExamSearch.class));
        hierarchyMap.put(CourseSearch.class,
                HierarchyIdentifierUtils.getClassHierarchy(CourseSearch.class));
    }

    public abstract SearchResponse search(SearchRequest searchRequest)
            throws IOException, TimeoutException;

    protected abstract ElasticRequest buildSearchRequest(SearchRequest searchRequest);

    protected abstract void populateSearchFields(SearchRequest searchRequest,
            ElasticRequest elasticRequest);

    protected abstract void populateFilterFields(SearchRequest searchRequest,
            ElasticRequest elasticRequest);

    protected abstract void populateAggregateFields(SearchRequest searchRequest,
            ElasticRequest elasticRequest);

    protected abstract void populateSortFields(SearchRequest searchRequest,
            ElasticRequest elasticRequest);

    protected abstract SearchResponse buildSearchResponse(
            ElasticResponse elasticResponse,
            ElasticRequest elasticRequest);

    protected ElasticResponse initiateSearch(ElasticRequest elasticRequest, Class type)
            throws IOException, TimeoutException {
        return searchService.search(elasticRequest, type);
    }

    protected ElasticRequest createSearchRequest(SearchRequest searchRequest) {
        ElasticRequest elasticRequest = new ElasticRequest();
        elasticRequest.setQueryTerm(searchRequest.getTerm());
        elasticRequest.setIndex(SEARCH_INDEX_INSTITUTE);
        elasticRequest.setAnalyzer(SEARCH_ANALYZER_INSTITUTE);
        elasticRequest.setSearchRequest(true);
        if (searchRequest.isFetchFilter()) {
            elasticRequest.setAggregationRequest(true);
        }
        elasticRequest.setLimit(searchRequest.getLimit());
        elasticRequest.setOffSet(searchRequest.getOffset());
        return elasticRequest;
    }

}
