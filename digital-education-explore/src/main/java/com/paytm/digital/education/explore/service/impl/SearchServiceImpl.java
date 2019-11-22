package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.enums.Client;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.explore.request.dto.search.SearchRequest;
import com.paytm.digital.education.explore.response.dto.search.InstituteData;
import com.paytm.digital.education.explore.response.dto.search.SearchBaseData;
import com.paytm.digital.education.explore.response.dto.search.SearchResponse;
import com.paytm.digital.education.explore.response.dto.search.SearchResult;
import com.paytm.digital.education.explore.response.dto.suggest.AutoSuggestData;
import com.paytm.digital.education.explore.response.dto.suggest.AutoSuggestResponse;
import com.paytm.digital.education.explore.response.dto.suggest.SuggestResult;
import com.paytm.digital.education.explore.service.RecentsSerivce;
import com.paytm.digital.education.explore.service.helper.LeadDetailHelper;
import com.paytm.digital.education.explore.service.helper.SubscriptionDetailHelper;
import com.paytm.digital.education.utility.JsonUtils;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

import static com.paytm.digital.education.enums.EducationEntity.COURSE;
import static com.paytm.digital.education.enums.EducationEntity.INSTITUTE;

@Service
@AllArgsConstructor
public class SearchServiceImpl {

    private static Logger log = LoggerFactory.getLogger(SearchServiceImpl.class);

    private InstituteSearchServiceImpl instituteSearchService;
    private LeadDetailHelper           leadDetailHelper;
    private SubscriptionDetailHelper   subscriptionDetailHelper;
    private ExamSearchServiceImpl      examSearchService;
    private CourseSearchService        courseSearchService;
    private RecentsSerivce             recentsSerivce;
    private SchoolSearchServiceImpl schoolSearchService;

    public SearchResponse search(SearchRequest searchRequest, Long userId, Client client) {
        long startTime = System.currentTimeMillis();
        log.debug("Starting search at : " + startTime);
        searchRequest.setClient(client);
        try {
            SearchResponse response = handler(searchRequest.getEntity()).search(searchRequest);
            log.debug("Search Response : {}", JsonUtils.toJson(response));

            if (Objects.nonNull(userId) && userId > 0 && response.isSearchResponse()) {

                if (StringUtils.isNotBlank(response.getSearchTerm()) && !CollectionUtils
                        .isEmpty(response.getResults().getValues())) {
                    recentsSerivce
                            .recordSearches(response.getSearchTerm(), userId, searchRequest.getEntity());
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
                }

            }
            log.debug("Time taken in search : " + (System.currentTimeMillis() - startTime));
            return response;
        } catch (Exception e) {
            log.error(
                    "ElasticSearch Failed. Search Request - {}, UserId - {}, Client - {}",
                    e, searchRequest, userId, client);
            return null;
        }
    }

    /**
     * Searches for top institutes
     * @param searchRequest represents SearchRequest body
     * @return AutoSuggestResponse
     * @throws Exception when userid is null
     */
    public AutoSuggestResponse instituteSearch(SearchRequest searchRequest) throws Exception {
        SearchResponse searchResponse = search(searchRequest, null, null);
        AutoSuggestResponse autoSuggestResponse = new AutoSuggestResponse();
        List<AutoSuggestData> asDataList = new ArrayList<>();

        //transform the search response to auto-suggest response
        if (Objects.nonNull(searchResponse)) {
            SearchResult results = searchResponse.getResults();
            if (Objects.nonNull(results) && Objects.nonNull(results.getEntity())) {
                if (results.getEntity().equals(INSTITUTE)) {
                    AutoSuggestData asData = new AutoSuggestData();
                    asData.setEntityType(INSTITUTE.name().toLowerCase());

                    List<SuggestResult> suggestResults = new ArrayList<>();
                    List<SearchBaseData> values = results.getValues();

                    if (!CollectionUtils.isEmpty(values)) {
                        for (SearchBaseData searchBaseData : values) {
                            InstituteData iData = (InstituteData) searchBaseData;

                            SuggestResult suggestResult = new SuggestResult(iData.getInstituteId(),
                                    iData.getOfficialName());
                            suggestResult.setOfficialAddress(iData.getOfficialAddress());
                            suggestResult.setEntityId(iData.getInstituteId());
                            suggestResult.setUrlDisplayName(iData.getUrlDisplayName());
                            suggestResult.setOfficialName(iData.getOfficialName());
                            suggestResult.setLogo(iData.getLogoUrl());

                            suggestResults.add(suggestResult);
                        }
                        asData.setResults(suggestResults);

                    }
                    asDataList.add(asData);
                    autoSuggestResponse.setData(asDataList);
                }
            }
        }
        return autoSuggestResponse;
    }

    private AbstractSearchServiceImpl handler(EducationEntity educationEntity) {
        switch (educationEntity) {
            case INSTITUTE:
                return instituteSearchService;
            case EXAM:
                return examSearchService;
            case COURSE:
                return courseSearchService;
            case SCHOOL:
                return schoolSearchService;
            default:
                throw new RuntimeException("Invalid entity requested.");
        }
    }

    private void updateShortlist(EducationEntity educationEntity, Long userId,
            Map<Long, SearchBaseData> searchBaseDataMap, List<Long> entityIds) {
        List<Long> subscribedEntities =
                subscriptionDetailHelper.getSubscribedEntities(educationEntity, userId, entityIds);

        if (!CollectionUtils.isEmpty(searchBaseDataMap)) {
            searchBaseDataMap.entrySet()
                    .forEach(entityEntry -> {
                        if (!CollectionUtils.isEmpty(subscribedEntities) && subscribedEntities
                                .contains(entityEntry.getKey())) {
                            entityEntry.getValue().setShortlisted(true);
                        } else {
                            entityEntry.getValue().setShortlisted(false);
                        }
                    });
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

        if (!CollectionUtils.isEmpty(searchBaseDataMap)) {
            searchBaseDataMap.entrySet()
                    .forEach(entityEntry -> {
                        if (!CollectionUtils.isEmpty(leadEntities) && leadEntities
                                .contains(entityEntry.getKey())) {
                            entityEntry.getValue().setInterested(true);
                        } else {
                            entityEntry.getValue().setInterested(false);
                        }
                    });
        }
    }
}
