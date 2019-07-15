package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.elasticsearch.enums.DataSortOrder;
import com.paytm.digital.education.elasticsearch.enums.FilterQueryType;
import com.paytm.digital.education.elasticsearch.models.*;
import com.paytm.digital.education.elasticsearch.service.ElasticSearchService;
import com.paytm.digital.education.explore.database.entity.SearchHistory;
import com.paytm.digital.education.explore.database.repository.SearchHistoryRepository;
import com.paytm.digital.education.explore.enums.ESIngestionStatus;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.response.dto.common.RecentSearch;
import com.paytm.digital.education.explore.service.RecentSearchesSerivce;
import com.paytm.digital.education.explore.utility.CommonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import static com.paytm.digital.education.explore.constants.ExploreConstants.*;

@Slf4j
@Service
@AllArgsConstructor
public class RecentSearchesServiceImpl implements RecentSearchesSerivce {

    private SearchHistoryRepository searchHistoryRepository;
    private ElasticSearchService    elasticSearchService;
    private Map<String, String>     recentSearchesClassLevelMap;


    @Override
    public void recordSearches(String searchTerm, Long userId, EducationEntity educationEntity) {
        String uniqueId = CommonUtil.convertNameToUrlDisplayName(searchTerm) + userId.toString();
        SearchHistory searchHistory = new SearchHistory();
        searchHistory.setRefId(uniqueId);
        searchHistory.setStatus(ESIngestionStatus.PENDING);
        searchHistory.setTerms(searchTerm);
        searchHistory.setCreatedAt(new Date());
        searchHistory.setUpdatedAt(new Date());
        searchHistory.setUserId(userId);
        searchHistory.setEducationEntity(educationEntity);
        searchHistoryRepository.save(searchHistory);
    }

    @Override
    public List<RecentSearch> getRecentSearchTerms(String term, Long userId, int size) {
        ElasticRequest searchRequest = buildRecentSearchrequest(term, size, userId);
        List<RecentSearch> recentSearches = new ArrayList<>();
        try {
            ElasticResponse<SearchHistory> searchResponse =
                    elasticSearchService.executeSearch(searchRequest, SearchHistory.class);
            if(!CollectionUtils.isEmpty(searchResponse.getDocuments())) {
                buildResponse(recentSearches, searchResponse);
            }
        } catch (IOException e) {
            log.error("Exception occured while querying Elasticsearch : {}",
                    e.getLocalizedMessage());
        } catch (TimeoutException e) {
            log.error("Connection timed out while querying Elasticsearch :{}",
                    e.getLocalizedMessage());
        }
        return recentSearches;
    }

    private void buildResponse(List<RecentSearch> recentSearches,
            ElasticResponse<SearchHistory> searchResponse) {
        List<SearchHistory> documents = searchResponse.getDocuments();
        for (SearchHistory document : documents) {
            RecentSearch recentSearch = new RecentSearch();
            recentSearch.setEntity(document.getEducationEntity());
            recentSearch.setTerm(document.getTerms());
            recentSearches.add(recentSearch);
        }
    }

    private ElasticRequest buildRecentSearchrequest(String term, int size, Long userId) {

        ElasticRequest elasticRequest = new ElasticRequest();
        elasticRequest.setIndex("recent_searches");
        elasticRequest.setAnalyzer(AUTOSUGGEST_ANALYZER);
        elasticRequest.setQueryTerm(term);
        elasticRequest.setOffSet(DEFAULT_OFFSET);
        elasticRequest.setLimit(size);
        elasticRequest.setAggregationRequest(false);

        FilterField[] filterFields = new FilterField[1];
        filterFields[0] = new FilterField();
        filterFields[0].setValues(Arrays.asList(userId));
        filterFields[0].setPath(recentSearchesClassLevelMap.get("user_id"));
        filterFields[0].setType(FilterQueryType.TERMS);
        filterFields[0].setName("user_id");
        elasticRequest.setFilterFields(filterFields);

        if (StringUtils.isBlank(term)) {
            SearchField[] searchFields = new SearchField[1];
            searchFields[0] = new SearchField();
            searchFields[0].setName("terms");
            searchFields[0].setPath(recentSearchesClassLevelMap.get("terms"));
            elasticRequest.setSearchFields(searchFields);
        } else {
            SortField[] sortFields = new SortField[1];
            sortFields[0] = new SortField();
            sortFields[0].setName("updated_at");
            sortFields[0].setPath(recentSearchesClassLevelMap.get("updated_at"));
            sortFields[0].setOrder(DataSortOrder.DESC);
            elasticRequest.setSortFields(sortFields);
        }

        return elasticRequest;
    }

}
