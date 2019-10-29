package com.paytm.digital.education.explore.scheduler;

import com.paytm.digital.education.constant.ExploreConstants;
import com.paytm.digital.education.enums.es.BulkRequestOperation;
import com.paytm.digital.education.elasticsearch.models.BulkRequestItem;
import com.paytm.digital.education.elasticsearch.service.ElasticSearchService;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.explore.request.dto.search.SearchRequest;
import com.paytm.digital.education.explore.response.dto.search.FilterBucket;
import com.paytm.digital.education.explore.response.dto.search.FilterData;
import com.paytm.digital.education.explore.response.dto.search.RecentSearch;
import com.paytm.digital.education.explore.response.dto.search.SearchBaseData;
import com.paytm.digital.education.explore.response.dto.search.SearchResponse;
import com.paytm.digital.education.explore.response.dto.search.TermFilterData;
import com.paytm.digital.education.explore.service.impl.RecentSearchServiceImpl;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

@EnableScheduling
@Configuration
public class RecentsLimitCheckScheduler {

    private static Logger log = LoggerFactory.getLogger(RecentsLimitCheckScheduler.class);

    @Autowired
    private RecentSearchServiceImpl recentSearchService;

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Value("${recent.search.limit.per.user}")
    private Integer esDocLimitPerUser;


    @Scheduled(fixedDelayString = "${search.limiter.interval}")
    public void extractUserId() {
        SearchResponse searchResponse = getAggregationsFromElastic();
        log.info("Elastic aggregations : {}", searchResponse);
        Long userId = extractUserIdFromElasticResponse(searchResponse);
        if (Objects.nonNull(userId)) {
            List<String> documentIds = getDocumentIds(userId);
            log.info("Removing documents: {} for user :{}", documentIds, userId);
            if (!CollectionUtils.isEmpty(documentIds)) {
                deleteDocuments(documentIds, ExploreConstants.RECENT_SEARCHES_ES_INDEX,
                        ExploreConstants.RECENT_SEARCHES_ES_TYPE);
            }
        }
    }

    private void deleteDocuments(List<String> documentIds, String index, String type) {

        Map<String, BulkRequestItem> documents = new HashMap<>();

        for (String documentId : documentIds) {
            BulkRequestItem bulkRequestItem = new BulkRequestItem();
            bulkRequestItem.setId(documentId);
            bulkRequestItem.setOperation(BulkRequestOperation.DELETE);
            bulkRequestItem.setType(type);
            bulkRequestItem.setIndex(index);
            documents.put(documentId, bulkRequestItem);
        }
        try {
            Map<String, String> bulkResponse = elasticSearchService.executeInBulk(documents);
            if (!CollectionUtils.isEmpty(bulkResponse)) {
                bulkResponse.forEach((id, item) -> {
                    log.warn("Couldn't delete doc: {} from: {} because: {}", id, index, item);
                });
            }
        } catch (IOException e) {
            log.error("Error in executing bulk {}", e.getLocalizedMessage());
        }
    }

    private List<String> getDocumentIds(Long userId) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setEntity(EducationEntity.RECENT_SEARCHES);
        searchRequest.setLimit(ExploreConstants.DELETE_RECENTS_BATCH_SIZE);
        searchRequest.setOffset(esDocLimitPerUser);
        searchRequest.setFetchFilter(false);
        Map<String, List<Object>> filters = new HashMap<>();
        filters.put(ExploreConstants.SEARCH_HISTORY_USERID, Arrays.asList(userId));
        searchRequest.setFilter(filters);
        try {
            SearchResponse searchResponse = recentSearchService.search(searchRequest);
            if (!CollectionUtils.isEmpty(searchResponse.getResults().getValues())) {
                List<SearchBaseData> searchHistories = searchResponse.getResults().getValues();
                List<String> documentIds = new ArrayList<>();
                for (SearchBaseData searchBaseData : searchHistories) {
                    RecentSearch searchHistory = (RecentSearch) searchBaseData;
                    documentIds.add(searchHistory.getId());
                }
                return documentIds;
            }
        } catch (IOException e) {
            log.error("IO exception while querying elasticsearch :{}", e.getLocalizedMessage());
        } catch (TimeoutException e) {
            log.error("Timed out  elasticsearch :{}", e.getLocalizedMessage());
        }
        return null;
    }

    private Long extractUserIdFromElasticResponse(SearchResponse searchResponse) {
        if (Objects.nonNull(searchResponse) && !CollectionUtils
                .isEmpty(searchResponse.getFilters())) {
            for (FilterData filterData : searchResponse.getFilters()) {
                if (ExploreConstants.SEARCH_HISTORY_USERID.equals(filterData.getName())) {
                    TermFilterData termFilterData = (TermFilterData) filterData;
                    if (!CollectionUtils.isEmpty(termFilterData.getBuckets())) {
                        FilterBucket bucket = termFilterData.getBuckets().get(0);
                        if (bucket.getDocCount() > esDocLimitPerUser) {
                            return Long.parseLong(bucket.getValue());
                        }
                    }
                }
            }
        }
        return null;
    }

    private SearchResponse getAggregationsFromElastic() {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setFetchFilter(true);
        searchRequest.setFetchSearchResults(false);
        searchRequest.setLimit(0);
        searchRequest.setEntity(EducationEntity.RECENT_SEARCHES);

        try {
            SearchResponse searchResponse = recentSearchService.search(searchRequest);
            return searchResponse;
        } catch (IOException e) {
            log.error("IO exception while querying elasticsearch :{}", e.getLocalizedMessage());
        } catch (TimeoutException e) {
            log.error("Timeed out  elasticsearch :{}", e.getLocalizedMessage());
        }
        return null;
    }


}
