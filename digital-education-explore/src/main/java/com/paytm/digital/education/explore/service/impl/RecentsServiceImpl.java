package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.elasticsearch.enums.BulkRequestOperation;
import com.paytm.digital.education.elasticsearch.models.BulkRequestItem;
import com.paytm.digital.education.elasticsearch.service.ElasticSearchService;
import com.paytm.digital.education.explore.constants.ExploreConstants;
import com.paytm.digital.education.explore.enums.RecentDocumentType;
import com.paytm.digital.education.explore.es.model.SearchHistoryEsDoc;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.kafka.KafkaProducer;
import com.paytm.digital.education.explore.request.dto.search.SearchRequest;
import com.paytm.digital.education.explore.response.dto.search.SearchResponse;
import com.paytm.digital.education.explore.service.RecentsSerivce;
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
public class RecentsServiceImpl implements RecentsSerivce {

    private ElasticSearchService    elasticSearchService;
    private KafkaProducer           kafkaProducer;
    private RecentSearchServiceImpl recentSearchService;

    @Override
    public void recordSearches(String searchTerm, Long userId, EducationEntity educationEntity) {
        String uniqueId = CommonUtil.convertNameToUrlDisplayName(searchTerm) + userId.toString();
        SearchHistoryEsDoc searchHistoryEsDoc = new SearchHistoryEsDoc();
        searchHistoryEsDoc.setId(uniqueId);
        searchHistoryEsDoc.setTerms(searchTerm);
        searchHistoryEsDoc.setCreatedAt(new Date());
        searchHistoryEsDoc.setUpdatedAt(new Date());
        searchHistoryEsDoc.setUserId(userId);
        searchHistoryEsDoc.setEducationEntity(educationEntity);
        searchHistoryEsDoc.setDocType(RecentDocumentType.RECENTS);
        log.info("Inserting :{} in kafka", searchHistoryEsDoc);
        kafkaProducer.sendMessage(ExploreConstants.RECENT_SEARCHES_kAFKA_TOPIC,
                JsonUtils.toJson(searchHistoryEsDoc));
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
    public void ingestAudits(List<SearchHistoryEsDoc> searchHistoryEsDocs) {
        Map<String, BulkRequestItem> documents = new HashMap<>();
        for (SearchHistoryEsDoc searchHistoryEsDoc : searchHistoryEsDocs) {
            BulkRequestItem bulkRequestItem = new BulkRequestItem();
            bulkRequestItem.setIndex(ExploreConstants.RECENT_SEARCHES_ES_INDEX);
            bulkRequestItem.setType(ExploreConstants.RECENT_SEARCHES_ES_TYPE);
            bulkRequestItem.setOperation(BulkRequestOperation.INDEX);
            bulkRequestItem.setSource(searchHistoryEsDoc);
            bulkRequestItem.setId(searchHistoryEsDoc.getId());
            documents.put(searchHistoryEsDoc.getId(), bulkRequestItem);
        }
        try {
            Map<String, String> ingestionResponse = elasticSearchService.executeInBulk(documents);
            processAuditIngestionResponse(ingestionResponse, documents);
        } catch (IOException e) {
            log.error("Unable to index documents : {}", e.getLocalizedMessage());
        }
    }

    private void processAuditIngestionResponse(Map<String, String> ingestionResponse,
            Map<String, BulkRequestItem> documents) {
        for (Map.Entry<String, String> entry : ingestionResponse.entrySet()) {
            SearchHistoryEsDoc
                    searchHistoryEsDoc =
                    (SearchHistoryEsDoc) documents.get(entry.getKey()).getSource();
            searchHistoryEsDoc.setFailureMessage(entry.getValue());
            searchHistoryEsDoc
                    .setEsIngestionRetries(searchHistoryEsDoc.getEsIngestionRetries() + 1);
            kafkaProducer.sendMessage(ExploreConstants.RECENT_SEARCHES_kAFKA_TOPIC,
                    JsonUtils.toJson(searchHistoryEsDoc));
        }
    }
}
