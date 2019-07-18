package com.paytm.digital.education.explore.scheduler;

import com.paytm.digital.education.elasticsearch.enums.BulkRequestOperation;
import com.paytm.digital.education.elasticsearch.models.BulkRequestItem;
import com.paytm.digital.education.elasticsearch.service.ElasticSearchService;
import com.paytm.digital.education.explore.constants.ExploreConstants;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.request.dto.search.SearchRequest;
import com.paytm.digital.education.explore.response.dto.search.RecentSearch;
import com.paytm.digital.education.explore.response.dto.search.SearchBaseData;
import com.paytm.digital.education.explore.response.dto.search.SearchResponse;
import com.paytm.digital.education.explore.response.dto.search.FilterBucket;
import com.paytm.digital.education.explore.response.dto.search.TermFilterData;
import com.paytm.digital.education.explore.response.dto.search.FilterData;
import com.paytm.digital.education.explore.service.impl.RecentSearchServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Objects;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

@EnableScheduling
@Configuration
@Slf4j
@AllArgsConstructor
public class RecentsLimitCheckScheduler {

    private RecentSearchServiceImpl recentSearchService;
    private ElasticSearchService    elasticSearchService;

    @Scheduled(fixedDelay = 10000)
    public void extractUserId() {
        log.info("Statring scheduler");
        SearchResponse searchResponse = getAggregationsFromElastic();
        Long userId = extractUserIdFromElasticResponse(searchResponse);
        log.info("User id : {}", userId);
        if (Objects.nonNull(userId)) {
            List<String> documentIds = getDocumentIds(userId);
            log.info("Found documents :{}", documentIds);
            deleteDocuments(documentIds, ExploreConstants.RECENT_SEARCHES_ES_INDEX,
                    ExploreConstants.RECENT_SEARCHES_ES_TYPE);
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
                    log.warn("Couldn't delete doc {} from {} because {}", id, index, item);
                });
            }
        } catch (IOException e) {
            log.error("Error in executing bulk {}", e.getLocalizedMessage());
        }
    }

    private List<String> getDocumentIds(Long userId) {

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setEntity(EducationEntity.RECENT_SEARCHES);
        searchRequest.setLimit(2);
        searchRequest.setOffset(1);
        searchRequest.setFetchFilter(false);
        searchRequest.setOffset(ExploreConstants.DEFAULT_OFFSET);
        Map<String, List<Object>> filters = new HashMap<>();
        filters.put(ExploreConstants.SEARCH_HISTORY_USERID, Arrays.asList(userId));
        searchRequest.setFilter(filters);
        searchRequest.setSource(false);
        SearchResponse searchResponse = null;
        try {
            searchResponse = recentSearchService.search(searchRequest);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        if (!CollectionUtils.isEmpty(searchResponse.getResults().getValues())) {
            List<SearchBaseData> searchHistories = searchResponse.getResults().getValues();
            List<String> documentIds = new ArrayList<>();
            for (SearchBaseData searchBaseData : searchHistories) {
                RecentSearch searchHistory = (RecentSearch) searchBaseData;
                if(searchHistory.getId()!=null) {
                    documentIds.add(searchHistory.getId());
                }
            }
            return documentIds;
        }
        return null;
    }

    private Long extractUserIdFromElasticResponse(SearchResponse searchResponse) {
        log.info("Agg response {}", searchResponse.toString());
        if (!CollectionUtils.isEmpty(searchResponse.getFilters())) {
            for (FilterData filterData : searchResponse.getFilters()) {
                if (ExploreConstants.SEARCH_HISTORY_USERID.equals(filterData.getName())) {
                    TermFilterData termFilterData = (TermFilterData) filterData;
                    if (!CollectionUtils.isEmpty(termFilterData.getBuckets())) {
                        FilterBucket bucket = termFilterData.getBuckets().get(0);
                        if (bucket.getDocCount() > 1) {
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
        searchRequest.setLimit(0);
        searchRequest.setEntity(EducationEntity.RECENT_SEARCHES);

        try {
            SearchResponse searchResponse = recentSearchService.search(searchRequest);
            return searchResponse;
        } catch (IOException e) {
            log.error("Could not connect to elasticsearch :{}", e.getLocalizedMessage());
        } catch (TimeoutException e) {
            log.error("Could not connect to elasticsearch :{}", e.getLocalizedMessage());
        }
        return null;
    }


}
