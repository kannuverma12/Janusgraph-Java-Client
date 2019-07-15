package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.request.dto.search.SearchRequest;
import com.paytm.digital.education.explore.response.dto.search.SearchBaseData;
import com.paytm.digital.education.explore.response.dto.search.SearchResponse;
import com.paytm.digital.education.explore.service.RecentSearchesSerivce;
import com.paytm.digital.education.explore.service.helper.LeadDetailHelper;
import com.paytm.digital.education.explore.service.helper.SubscriptionDetailHelper;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.paytm.digital.education.explore.enums.EducationEntity.COURSE;
import static com.paytm.digital.education.explore.enums.EducationEntity.INSTITUTE;

@Slf4j
@Service
@AllArgsConstructor
public class SearchServiceImpl {

    private InstituteSearchServiceImpl instituteSearchService;
    private LeadDetailHelper           leadDetailHelper;
    private SubscriptionDetailHelper   subscriptionDetailHelper;
    private ExamSearchServiceImpl      examSearchService;
    private CourseSearchService        courseSearchService;
    private RecentSearchesSerivce      recentSearchesSerivce;

    public SearchResponse search(SearchRequest searchRequest, Long userId) throws Exception {
        long startTime = System.currentTimeMillis();
        log.debug("Starting search at : " + startTime);
        SearchResponse response = handler(searchRequest.getEntity()).search(searchRequest);
        log.debug("Search Response : {}", JsonUtils.toJson(response));

        if (Objects.nonNull(userId) && userId > 0 && response.isSearchResponse()) {

            if (StringUtils.isNotBlank(searchRequest.getTerm()) && !CollectionUtils
                    .isEmpty(response.getResults().getValues())) {
                recentSearchesSerivce
                        .recordSearches(searchRequest.getTerm(), userId, searchRequest.getEntity());
            }

            if (!CollectionUtils.isEmpty(response.getEntityDataMap())) {
                Map<Long, SearchBaseData> searchBaseDataMap = response.getEntityDataMap();
                List<Long> entityIds = new ArrayList<>(searchBaseDataMap.keySet());
                EducationEntity entity = searchRequest.getEntity();
                if (entity.equals(COURSE)) {
                    updateShortlist(INSTITUTE, userId, searchBaseDataMap, entityIds);
                } else {
                    updateShortlist(entity, userId, searchBaseDataMap, entityIds);
                }
                updateInterested(searchRequest.getEntity(), userId, searchBaseDataMap, entityIds);
                response.getEntityDataMap().clear();
            }

        }
        log.debug("Time taken in search : " + (System.currentTimeMillis() - startTime));
        return response;
    }

    private AbstractSearchServiceImpl handler(EducationEntity educationEntity) {
        switch (educationEntity) {
            case INSTITUTE:
                return instituteSearchService;
            case EXAM:
                return examSearchService;
            case COURSE:
                return courseSearchService;
            default:
                throw new RuntimeException("Invalid entity requested.");
        }
    }

    private void updateShortlist(EducationEntity educationEntity, Long userId,
            Map<Long, SearchBaseData> searchBaseDataMap, List<Long> entityIds) {
        List<Long> subscribedEntities =
                subscriptionDetailHelper.getSubscribedEntities(educationEntity, userId, entityIds);
        if (!CollectionUtils.isEmpty(subscribedEntities)) {
            subscribedEntities
                    .forEach(entityId -> searchBaseDataMap.get(entityId).setShortlisted(true));
        }
    }

    private void updateInterested(EducationEntity educationEntity, Long userId,
            Map<Long, SearchBaseData> searchBaseDataMap, List<Long> entityIds) {
        List<Long> leadEntities;
        if (EducationEntity.EXAM.equals(educationEntity)) {
            leadEntities = leadDetailHelper.getLeadEntities(educationEntity, userId, entityIds);
        } else {
            leadEntities =
                    leadDetailHelper.getInterestedLeadInstituteIds(userId, entityIds);
        }
        if (!CollectionUtils.isEmpty(leadEntities)) {
            leadEntities.forEach(entityId -> searchBaseDataMap.get(entityId).setInterested(true));
        }
    }
}
