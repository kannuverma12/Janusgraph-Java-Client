package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.explore.database.entity.Subscription;
import com.paytm.digital.education.explore.database.repository.SubscriptionRepository;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.enums.SubscribableEntityType;
import com.paytm.digital.education.explore.request.dto.search.SearchRequest;
import com.paytm.digital.education.explore.response.dto.search.SearchBaseData;
import com.paytm.digital.education.explore.response.dto.search.SearchResponse;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class SearchServiceImpl {

    private InstituteSearchServiceImpl instituteSearchService;
    private SubscriptionRepository     subscriptionRepository;

    public SearchResponse search(SearchRequest searchRequest, Long userId) throws Exception {
        SearchResponse response = handler(searchRequest.getEntity()).search(searchRequest);
        log.debug("Search Response : {}", JsonUtils.toJson(response));

        if (userId != null && userId > 0 && response.isSearchResponse()) {
            Map<Long, SearchBaseData> searchBaseDataMap = response.getEntityDataMap();
            List<Long> entityIds = new ArrayList<>(searchBaseDataMap.keySet());
            SubscribableEntityType subscribableEntityType =
                    EducationEntity.convertToSubscribableEntity(searchRequest.getEntity());
            List<Subscription> subscribedEntities =
                    subscriptionRepository.findBySubscribableEntityTypeAndUserIdAndEntityIdIn(
                            subscribableEntityType, entityIds, userId);
            if (!CollectionUtils.isEmpty(subscribedEntities)) {
                for (Subscription subscription : subscribedEntities) {
                    searchBaseDataMap.get(subscription.getEntityId()).setShortlisted(true);
                }
            }
        }
        response.getEntityDataMap().clear();
        return response;
    }

    private AbstractSearchServiceImpl handler(EducationEntity educationEntity) {
        switch (educationEntity) {
            case INSTITUTE:
                return instituteSearchService;
            default:
                throw new RuntimeException("Invalid entity requested.");
        }
    }
}
