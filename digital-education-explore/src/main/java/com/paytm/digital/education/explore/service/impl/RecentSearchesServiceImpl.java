package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.elasticsearch.models.IndexObject;
import com.paytm.digital.education.elasticsearch.service.ElasticSearchService;
import com.paytm.digital.education.explore.constants.ExploreConstants;
import com.paytm.digital.education.explore.enums.RecentDocumentType;
import com.paytm.digital.education.explore.es.model.SearchHistory;
import com.paytm.digital.education.explore.enums.ESIngestionStatus;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.kafka.KafkaProducer;
import com.paytm.digital.education.explore.request.dto.search.SearchRequest;
import com.paytm.digital.education.explore.response.dto.search.SearchResponse;
import com.paytm.digital.education.explore.service.RecentSearchesSerivce;
import com.paytm.digital.education.explore.utility.CommonUtil;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@AllArgsConstructor
public class RecentSearchesServiceImpl implements RecentSearchesSerivce {

    private ElasticSearchService    elasticSearchService;
    private KafkaProducer           kafkaProducer;
    private RecentSearchServiceImpl recentSearchService;

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
        kafkaProducer.sendMessage(ExploreConstants.RECENT_SEARCHES_kAFKA_TOPIC,
                JsonUtils.toJson(searchHistory));
    }

    @Override
    public SearchResponse getRecentSearchTerms(String term, Long userId, int size) {

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setTerm(term);
        Map<String, List<Object>> filters = new HashMap<>();
        filters.put(ExploreConstants.SEARCH_HISTORY_USERID, Arrays.asList(userId));
        searchRequest.setFilter(filters);
        searchRequest.setEntity(EducationEntity.RECENT_SEARCHES);
        searchRequest.setOffset(ExploreConstants.DEFAULT_OFFSET);
        searchRequest.setLimit(size);
        searchRequest.setFetchFilter(false);

        try {
            return recentSearchService.search(searchRequest);
        } catch (IOException e) {
            log.error("Exception occured while querying Elasticsearch : {}",
                    e.getLocalizedMessage());
        } catch (TimeoutException e) {
            log.error("Connection timed out while querying Elasticsearch :{}",
                    e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public void ingestAudits(List<SearchHistory> searchHistories) {
        Map<String, IndexObject> documents = getDocuments(searchHistories);
        try {
            Map<String, String> ingestionResponse = elasticSearchService.ingest(documents);
            log.debug("Es Ingestion response {}", ingestionResponse);
            processAuditIngestionResponse(ingestionResponse, documents);
        } catch (IOException e) {
            log.error("Unable to connect to elasticsearch {}.", e.getLocalizedMessage());
        }
    }

    private void processAuditIngestionResponse(Map<String, String> ingestionResponse,
            Map<String, IndexObject> documents) {
        for (Map.Entry<String, String> entry : ingestionResponse.entrySet()) {
            SearchHistory searchHistory = (SearchHistory) documents.get(entry.getKey()).getSource();
            searchHistory.setFailureMessage(entry.getValue());
            searchHistory.setEsIngestionRetries(searchHistory.getEsIngestionRetries() + 1);
            kafkaProducer.sendMessage(ExploreConstants.RECENT_SEARCHES_kAFKA_TOPIC,
                    JsonUtils.toJson(searchHistory));
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
}
