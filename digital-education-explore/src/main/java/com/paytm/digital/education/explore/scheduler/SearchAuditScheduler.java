package com.paytm.digital.education.explore.scheduler;

import com.paytm.digital.education.elasticsearch.models.IndexObject;
import com.paytm.digital.education.elasticsearch.service.ElasticSearchService;
import com.paytm.digital.education.explore.database.entity.SearchHistory;
import com.paytm.digital.education.explore.database.repository.SearchHistoryRepository;
import com.paytm.digital.education.explore.enums.ESIngestionStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;

@Component
@AllArgsConstructor
@Slf4j
public class SearchAuditScheduler {

    private ElasticSearchService elasticSearchService;

    private SearchHistoryRepository searchHistoryRepository;

    @Scheduled(fixedDelay = 30000)
    public void ingestFailedSearchAudit() {
        ingestAudits(ESIngestionStatus.FAILED);
    }

    @Scheduled(fixedDelay = 10000)
    public void ingestPendingSearchaudit() {
        ingestAudits(ESIngestionStatus.PENDING);
    }

    private void ingestAudits(ESIngestionStatus status) {

        List<SearchHistory> pendingSearchHistories = searchHistoryRepository.findManyBystatus(status);
        log.info("Found docs in DB : {}", pendingSearchHistories.toString());

        if (!CollectionUtils.isEmpty(pendingSearchHistories)) {
            Map<String, IndexObject> indexObjects = new HashMap<>();
            for (SearchHistory searchHistory : pendingSearchHistories) {
                IndexObject indexObject = new IndexObject();
                indexObject.setId(searchHistory.getRefId());
                indexObject.setIndex("recent_searches");
                indexObject.setType("education");
                indexObject.setSource(searchHistory);
                indexObjects.put(indexObject.getId(), indexObject);
            }
            try {
                Map<String, String> bulkResponseFailures =
                        elasticSearchService.ingest(indexObjects);
                updateStatus(bulkResponseFailures, pendingSearchHistories, false);
            } catch (IOException e) {
                e.printStackTrace();
                log.error("Error in ingesting documents into elasticsearch {}", e.getLocalizedMessage());
                updateStatus(null, pendingSearchHistories, true);
            }
        }

    }

    private void updateStatus(Map<String, String> bulkResponseFailures,
            List<SearchHistory> pendingSearchHistories, boolean exception) {
        for (SearchHistory searchHistory : pendingSearchHistories) {
            if (exception == true || bulkResponseFailures.containsKey(searchHistory.getRefId())) {
                searchHistory.setStatus(ESIngestionStatus.FAILED);
            } else {
                searchHistory.setStatus(ESIngestionStatus.SUCCESS);
            }
            searchHistory.setUpdatedAt(new Date());
            searchHistoryRepository.save(searchHistory);
        }
    }

}
