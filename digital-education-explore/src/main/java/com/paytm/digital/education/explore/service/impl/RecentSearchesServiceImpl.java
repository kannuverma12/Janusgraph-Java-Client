package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.elasticsearch.enums.DataSortOrder;
import com.paytm.digital.education.elasticsearch.enums.FilterQueryType;
import com.paytm.digital.education.elasticsearch.models.FilterField;
import com.paytm.digital.education.elasticsearch.models.SearchField;
import com.paytm.digital.education.elasticsearch.models.SortField;
import com.paytm.digital.education.elasticsearch.models.IndexObject;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.elasticsearch.service.ElasticSearchService;
import com.paytm.digital.education.explore.constants.ExploreConstants;
import com.paytm.digital.education.explore.enums.RecentDocumentType;
import com.paytm.digital.education.explore.es.model.SearchHistory;
import com.paytm.digital.education.explore.enums.ESIngestionStatus;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.kafka.KafkaProducer;
import com.paytm.digital.education.explore.response.dto.common.RecentSearch;
import com.paytm.digital.education.explore.service.RecentSearchesSerivce;
import com.paytm.digital.education.explore.utility.CommonUtil;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@AllArgsConstructor
public class RecentSearchesServiceImpl implements RecentSearchesSerivce {

    private ElasticSearchService elasticSearchService;
    private KafkaProducer        kafkaProducer;
    private Map<String, String>  recentSearchesClassLevelMap;

    @Override
    public void recordSearches(String searchTerm, Long userId, EducationEntity educationEntity) {
        String uniqueId = CommonUtil.convertNameToUrlDisplayName(searchTerm) + userId.toString();
        SearchHistory searchHistory = new SearchHistory();
        searchHistory.setId(uniqueId);
        searchHistory.setStatus(ESIngestionStatus.PENDING);
        searchHistory.setTerms(searchTerm);
        searchHistory.setCreatedAt(new Date());
        searchHistory.setUpdatedAt(new Date());
        searchHistory.setUserId(userId);
        searchHistory.setEducationEntity(educationEntity);
        searchHistory.setDocType(RecentDocumentType.RECENTS);
        kafkaProducer.sendMessage(ExploreConstants.RECENT_SEARCHES_kAFKA_TOPIC, JsonUtils.toJson(searchHistory));
    }

    @Override
    public List<RecentSearch> getRecentSearchTerms(String term, Long userId, int size) {
        ElasticRequest searchRequest = buildRecentSearchRequest(term, size, userId);
        List<RecentSearch> recentSearches = new ArrayList<>();
        try {
            ElasticResponse<SearchHistory> searchResponse =
                    elasticSearchService.executeSearch(searchRequest, SearchHistory.class);
            if (!CollectionUtils.isEmpty(searchResponse.getDocuments())) {
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

    @Override
    public void ingestAudits(List<SearchHistory> searchHistories){
        Map<String, IndexObject> documents = getDocuments(searchHistories);
        try {
            Map<String, String> ingestionResponse = elasticSearchService.ingest(documents);
            log.debug("Es Ingestion response {}", ingestionResponse);
            processAuditIngestionResponse(ingestionResponse, documents);
        } catch (IOException e) {
            log.error("Unable to connect to elasticsearch {}.", e.getLocalizedMessage());
        }
    }

    private void processAuditIngestionResponse(Map<String, String> ingestionResponse,  Map<String, IndexObject> documents ){
        for(Map.Entry<String, String> entry : ingestionResponse.entrySet()){
            SearchHistory searchHistory = (SearchHistory)documents.get(entry.getKey()).getSource();
            searchHistory.setFailureMessage(entry.getValue());
            searchHistory.setEsIngestionRetries(searchHistory.getEsIngestionRetries()+1);
            kafkaProducer.sendMessage(ExploreConstants.RECENT_SEARCHES_kAFKA_TOPIC, JsonUtils.toJson(searchHistory));
        }
    }

    private Map<String, IndexObject> getDocuments(List<SearchHistory> searchHistories) {
        Map<String, IndexObject> documents = new HashMap<>();
        for (SearchHistory searchHistory : searchHistories) {
            IndexObject indexObject = new IndexObject();
            indexObject.setId(searchHistory.getId());
            indexObject.setIndex(ExploreConstants.RECENT_SEARCHES_ES_INDEX);
            indexObject.setType(ExploreConstants.RECENT_SEARCHES_ES_TYPE);
            indexObject.setSource(searchHistory);
            documents.put(indexObject.getId(), indexObject);
        }
        return documents;
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

    private ElasticRequest buildRecentSearchRequest(String term, int size, Long userId) {

        ElasticRequest elasticRequest = new ElasticRequest();
        elasticRequest.setIndex(ExploreConstants.RECENT_SEARCHES_ES_INDEX);
        elasticRequest.setAnalyzer(ExploreConstants.AUTOSUGGEST_ANALYZER);
        elasticRequest.setQueryTerm(term);
        elasticRequest.setOffSet(ExploreConstants.DEFAULT_OFFSET);
        elasticRequest.setLimit(size);
        elasticRequest.setSearchRequest(true);
        elasticRequest.setAggregationRequest(false);

        FilterField[] filterFields = new FilterField[1];
        filterFields[0] = new FilterField();
        filterFields[0].setValues(Arrays.asList(userId));
        filterFields[0].setPath(recentSearchesClassLevelMap.get(ExploreConstants.SEARCH_HISTORY_USERID));
        filterFields[0].setType(FilterQueryType.TERMS);
        filterFields[0].setName(ExploreConstants.SEARCH_HISTORY_USERID);
        elasticRequest.setFilterFields(filterFields);

        if (StringUtils.isNotBlank(term)) {
            SearchField[] searchFields = new SearchField[1];
            searchFields[0] = new SearchField();
            searchFields[0].setName(ExploreConstants.SEARCH_HISTORY_TERMS);
            searchFields[0].setPath(recentSearchesClassLevelMap.get(ExploreConstants.SEARCH_HISTORY_TERMS));
            elasticRequest.setSearchFields(searchFields);
        } else {
            SortField[] sortFields = new SortField[1];
            sortFields[0] = new SortField();
            sortFields[0].setName(ExploreConstants.SEARCH_HISTORY_UPDATEDAT);
            sortFields[0].setPath(recentSearchesClassLevelMap.get(ExploreConstants.SEARCH_HISTORY_UPDATEDAT));
            sortFields[0].setOrder(DataSortOrder.DESC);
            elasticRequest.setSortFields(sortFields);
        }

        return elasticRequest;
    }

}
