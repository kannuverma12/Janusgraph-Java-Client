package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.elasticsearch.enums.FilterQueryType;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.explore.constants.ExploreConstants;
import com.paytm.digital.education.explore.es.model.SearchHistory;
import com.paytm.digital.education.explore.request.dto.search.SearchRequest;
import com.paytm.digital.education.explore.response.dto.search.RecentSearch;
import com.paytm.digital.education.explore.response.dto.search.SearchBaseData;
import com.paytm.digital.education.explore.response.dto.search.SearchResponse;
import com.paytm.digital.education.explore.response.dto.search.SearchResult;
import com.paytm.digital.education.explore.service.helper.SearchAggregateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Service
public class RecentSearchServiceImpl extends AbstractSearchServiceImpl {

    private static Map<String, Float>           searchFieldKeys;
    private static Map<String, FilterQueryType> filterQueryTypeMap;

    @Autowired
    private SearchAggregateHelper searchAggregateHelper;


    @PostConstruct
    private void init() {
        searchFieldKeys = new HashMap<>();
        searchFieldKeys.put(ExploreConstants.SEARCH_HISTORY_TERMS, 1F);
        filterQueryTypeMap = new HashMap<>();
        filterQueryTypeMap.put(ExploreConstants.SEARCH_HISTORY_USERID, FilterQueryType.TERMS);
    }

    @Override
    public SearchResponse search(SearchRequest searchRequest)
            throws IOException, TimeoutException {
        ElasticRequest elasticRequest = buildSearchRequest(searchRequest);
        ElasticResponse elasticResponse = initiateSearch(elasticRequest, SearchHistory.class);

        SearchResponse searchResponse = new SearchResponse();
        populateSearchResults(searchResponse, elasticResponse, null);
        return searchResponse;
    }

    @Override
    protected ElasticRequest buildSearchRequest(SearchRequest searchRequest) {
        ElasticRequest elasticRequest =
                createSearchRequest(searchRequest, ExploreConstants.AUTOSUGGEST_ANALYZER,
                        ExploreConstants.RECENT_SEARCHES_ES_INDEX);
        Map<String, Float> searchKeys = searchFieldKeys;
        populateSearchFields(searchRequest, elasticRequest, searchKeys, SearchHistory.class);
        populateFilterFields(searchRequest, elasticRequest, SearchHistory.class,
                filterQueryTypeMap);
        populateAggregateFields(searchRequest, elasticRequest,
                searchAggregateHelper.gerRecentSearchesAggregateData(), SearchHistory.class);
        populateSortFields(searchRequest, elasticRequest, SearchHistory.class);
        return elasticRequest;
    }

    @Override
    protected void populateSearchResults(SearchResponse searchResponse,
            ElasticResponse elasticResponse, Map<String, Map<String, Object>> properties) {

        List<SearchHistory> documents = elasticResponse.getDocuments();
        List<SearchBaseData> recentSearches = new ArrayList<>();
        for (SearchHistory document : documents) {
            RecentSearch recentSearch = new RecentSearch();
            recentSearch.setEntity(document.getEducationEntity());
            recentSearch.setTerm(document.getTerms());
            recentSearches.add(recentSearch);
        }
        SearchResult searchResult = new SearchResult();
        searchResult.setValues(recentSearches);
        searchResponse.setResults(searchResult);
    }
}
