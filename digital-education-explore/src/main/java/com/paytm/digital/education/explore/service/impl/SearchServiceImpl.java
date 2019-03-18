package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.request.dto.search.SearchRequest;
import com.paytm.digital.education.explore.response.dto.search.SearchBaseData;
import com.paytm.digital.education.explore.response.dto.search.SearchResponse;
import com.paytm.digital.education.explore.service.helper.LeadDetailHelper;
import com.paytm.digital.education.explore.service.helper.SubscriptionDetailHelper;
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
    private LeadDetailHelper           leadDetailHelper;
    private SubscriptionDetailHelper   subscriptionDetailHelper;
    private ExamSearchServiceImpl      examSearchService;

    public SearchResponse search(SearchRequest searchRequest, Long userId) throws Exception {
        SearchResponse response = handler(searchRequest.getEntity()).search(searchRequest);
        log.debug("Search Response : {}", JsonUtils.toJson(response));

        if (userId != null && userId > 0 && response.isSearchResponse()
                && !CollectionUtils.isEmpty(response.getEntityDataMap())) {
            Map<Long, SearchBaseData> searchBaseDataMap = response.getEntityDataMap();
            List<Long> entityIds = new ArrayList<>(searchBaseDataMap.keySet());
            updateShortlist(searchRequest.getEntity(), userId, searchBaseDataMap, entityIds);
            updateGetInTouch(searchRequest.getEntity(), userId, searchBaseDataMap, entityIds);
            response.getEntityDataMap().clear();
        }
        return response;
    }

    private AbstractSearchServiceImpl handler(EducationEntity educationEntity) {
        switch (educationEntity) {
            case INSTITUTE:
                return instituteSearchService;
            case EXAM:
                return examSearchService;
            default:
                throw new RuntimeException("Invalid entity requested.");
        }
    }

    private void updateShortlist(EducationEntity educationEntity, Long userId,
            Map<Long, SearchBaseData> searchBaseDataMap, List<Long> entityIds) {
        List<Long> subscribedEntities =
                subscriptionDetailHelper.getSubscribedEntities(educationEntity, userId, entityIds);
        if (!CollectionUtils.isEmpty(subscribedEntities)) {
            subscribedEntities.forEach(entityId -> searchBaseDataMap.get(entityId).setShortlisted(true));
        }
    }

    private void updateGetInTouch(EducationEntity educationEntity, Long userId,
            Map<Long, SearchBaseData> searchBaseDataMap, List<Long> entityIds) {
        List<Long> leadEntities = leadDetailHelper.getLeadEntities(educationEntity, userId, entityIds);
        if (!CollectionUtils.isEmpty(leadEntities)) {
            leadEntities.forEach(entityId -> searchBaseDataMap.get(entityId).setGetInTouch(true));
        }
    }
}
