package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.elasticsearch.enums.DataSortOrder;
import com.paytm.digital.education.elasticsearch.enums.FilterQueryType;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.explore.constants.ExploreConstants;
import com.paytm.digital.education.explore.es.model.SearchHistoryEsDoc;
import com.paytm.digital.education.explore.request.dto.search.SearchRequest;
import com.paytm.digital.education.explore.response.dto.search.RecentSearch;
import com.paytm.digital.education.explore.response.dto.search.SearchBaseData;
import com.paytm.digital.education.explore.response.dto.search.SearchResponse;
import com.paytm.digital.education.explore.response.dto.search.SearchResult;
import com.paytm.digital.education.explore.service.helper.SearchAggregateHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
public class RecentSearchServiceImpl extends AbstractSearchServiceImpl {

    private static  Map<String, Float>                   searchFieldKeys;
    private static  Map<String, FilterQueryType>         filterQueryTypeMap;
    private static LinkedHashMap<String, DataSortOrder> defaultSortKeysInOrder;

    @Autowired
    private SearchAggregateHelper searchAggregateHelper;


    @PostConstruct
    private void init() {
        searchFieldKeys = new HashMap<>();
        searchFieldKeys.put(ExploreConstants.SEARCH_HISTORY_TERMS, 1F);
        filterQueryTypeMap = new HashMap<>();
        filterQueryTypeMap.put(ExploreConstants.SEARCH_HISTORY_USERID, FilterQueryType.TERMS);
        filterQueryTypeMap.put(ExploreConstants.RECENT_SEARCHES_ENTITY, FilterQueryType.TERMS);
        defaultSortKeysInOrder = new LinkedHashMap<>();
        defaultSortKeysInOrder.put(ExploreConstants.SEARCH_HISTORY_UPDATEDAT, DataSortOrder.DESC);
    }

    @Override
    public SearchResponse search(SearchRequest searchRequest)
            throws IOException, TimeoutException {
        ElasticRequest elasticRequest = buildSearchRequest(searchRequest);
        ElasticResponse elasticResponse = initiateSearch(elasticRequest, SearchHistoryEsDoc.class);
        SearchResponse searchResponse = new SearchResponse(searchRequest.getTerm());
        buildSearchResponse(searchResponse, elasticResponse, elasticRequest, null, null, null,
                null);
        return searchResponse;
    }

    @Override
    protected ElasticRequest buildSearchRequest(SearchRequest searchRequest) {
        ElasticRequest elasticRequest =
                createSearchRequest(searchRequest, ExploreConstants.AUTOSUGGEST_ANALYZER,
                        ExploreConstants.RECENT_SEARCHES_ES_INDEX);
        populateSearchFields(searchRequest, elasticRequest, searchFieldKeys,
                SearchHistoryEsDoc.class);
        populateFilterFields(searchRequest, elasticRequest, SearchHistoryEsDoc.class,
                filterQueryTypeMap);
        if (searchRequest.getFetchFilter()) {
            populateAggregateFields(searchRequest, elasticRequest,
                    searchAggregateHelper.gerRecentSearchesAggregateData(),
                    SearchHistoryEsDoc.class);
        }
        if (StringUtils.isBlank(searchRequest.getTerm())) {
            searchRequest.setSortOrder(defaultSortKeysInOrder);
            populateSortFields(searchRequest, elasticRequest, SearchHistoryEsDoc.class);
        }
        return elasticRequest;
    }

    @Override
    protected void populateSearchResults(SearchResponse searchResponse,
            ElasticResponse elasticResponse, Map<String, Map<String, Object>> properties,
            ElasticRequest elasticRequest) {

        List<SearchHistoryEsDoc> documents = elasticResponse.getDocuments();
        List<SearchBaseData> recentSearches = new ArrayList<>();
        for (SearchHistoryEsDoc document : documents) {
            RecentSearch recentSearch = new RecentSearch();
            recentSearch.setEntity(document.getEducationEntity());
            recentSearch.setTerm(document.getTerms());
            recentSearch.setId(document.getId());
            recentSearches.add(recentSearch);
        }
        SearchResult searchResult = new SearchResult();
        searchResult.setValues(recentSearches);
        searchResponse.setResults(searchResult);
    }
}
