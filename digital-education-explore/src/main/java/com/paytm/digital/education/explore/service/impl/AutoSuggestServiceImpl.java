package com.paytm.digital.education.explore.service.impl;

import static com.paytm.digital.education.constant.ExploreConstants.DEFAULT_AUTOSUGGEST_SIZE;
import static com.paytm.digital.education.constant.ExploreConstants.DEFAULT_AUTOSUGGEST_COMPARE;
import static com.paytm.digital.education.constant.ExploreConstants.ENTITY_TYPE;

import com.paytm.digital.education.elasticsearch.models.AggregationResponse;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.elasticsearch.models.TopHitsAggregationResponse;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.explore.enums.UserAction;
import com.paytm.digital.education.search.model.AutoSuggestEsData;
import com.paytm.digital.education.explore.response.dto.suggest.AutoSuggestData;
import com.paytm.digital.education.explore.response.dto.suggest.AutoSuggestResponse;
import com.paytm.digital.education.explore.response.dto.suggest.SuggestResult;
import com.paytm.digital.education.explore.service.helper.ExamLogoHelper;
import com.paytm.digital.education.explore.service.helper.SubscriptionDetailHelper;
import com.paytm.digital.education.search.service.CommonAutoSuggestionService;
import com.paytm.digital.education.utility.CommonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Slf4j
@AllArgsConstructor
@Service
public class AutoSuggestServiceImpl {

    private CommonAutoSuggestionService commonAutoSuggestService;
    private SubscriptionDetailHelper    subscriptionDetailHelper;
    private ExamLogoHelper              examLogoHelper;

    public AutoSuggestResponse getAll(List<EducationEntity> entities, boolean alphabeticalSorting,
            int limit) {
        AutoSuggestResponse autoSuggestResponse =
                getSuggestResults(null, entities, limit, alphabeticalSorting);
        return autoSuggestResponse;
    }

    public AutoSuggestResponse getSuggestions(String searchTerm, List<EducationEntity> entities,
            List<UserAction> actions, Long userId) {
        AutoSuggestResponse autoSuggestResponse = null;
        if (!CollectionUtils.isEmpty(actions) && actions.contains(UserAction.SHORTLIST)) {
            autoSuggestResponse =
                    getSuggestResults(searchTerm, entities, DEFAULT_AUTOSUGGEST_COMPARE);
        } else {
            autoSuggestResponse = getSuggestResults(searchTerm, entities, DEFAULT_AUTOSUGGEST_SIZE);
        }
        groupEntityBasedOnActions(autoSuggestResponse, actions, userId);
        return autoSuggestResponse;
    }

    @Cacheable(value = "autosuggest")
    public AutoSuggestResponse getSuggestResults(String searchTerm,
            List<EducationEntity> entities, int size) {
        ElasticResponse<AutoSuggestEsData> esResponse =
                commonAutoSuggestService.suggest(searchTerm, entities, size);
        return buildAutoSuggestResponse(entities, esResponse);
    }

    private AutoSuggestResponse buildAutoSuggestResponse(List<EducationEntity> entities,
            ElasticResponse<AutoSuggestEsData> esResponse) {
        if (entities.size() == 1) {
            return extractDataFromSearchResults(entities.get(0), esResponse);
        } else {
            return extractDataFromTopHitsResponse(esResponse);
        }
    }

    private List<SuggestResult> convertEsDataToResponse(String entity,
            List<AutoSuggestEsData> documents,
            Map<String, Map<Long, SuggestResult>> entityResultsMap) {
        List<SuggestResult> responseResultDocList = new ArrayList<>();
        documents.forEach(esDocument -> {
            SuggestResult responseDoc = new SuggestResult(esDocument.getEntityId(),
                    esDocument.getOfficialName());
            if (EducationEntity.LOCATION.equals(esDocument.getEntityType())) {
                responseDoc.setCityId(esDocument.getCityId());
                responseDoc.setStateId(esDocument.getStateId());
            }
            responseDoc.setUrlDisplayName(
                    CommonUtil.convertNameToUrlDisplayName(esDocument.getOfficialName()));

            if (EducationEntity.EXAM.equals(esDocument.getEntityType())) {
                responseDoc.setLogo(examLogoHelper
                        .getExamLogoUrl(esDocument.getEntityId(), esDocument.getLogo()));
            } else {
                if (StringUtils.isNotBlank(esDocument.getLogo())) {
                    responseDoc.setLogo(CommonUtil
                            .getLogoLink(esDocument.getLogo(), EducationEntity.EXAM));
                }
            }
            if (Objects.nonNull(esDocument.getOfficialAddress())) {
                responseDoc.setOfficialAddress(
                        CommonUtil
                                .getOfficialAddress(
                                        esDocument.getOfficialAddress().getState(),
                                        esDocument.getOfficialAddress().getCity(), null,
                                        null,
                                        null));
            }
            entityResultsMap.get(entity).put(responseDoc.getEntityId(), responseDoc);
            responseResultDocList.add(responseDoc);
        });
        return responseResultDocList;
    }

    private AutoSuggestResponse extractDataFromTopHitsResponse(
            ElasticResponse<AutoSuggestEsData> esResponse) {

        Map<String, AggregationResponse> aggregationResponse = esResponse.getAggregationResponse();

        if (!aggregationResponse.containsKey(ENTITY_TYPE)) {
            log.error("Aggregations not found for given entity type.");
        }

        TopHitsAggregationResponse<AutoSuggestEsData> topHitsPerEntity =
                (TopHitsAggregationResponse<AutoSuggestEsData>) aggregationResponse
                        .get(ENTITY_TYPE);

        List<AutoSuggestData> suggestData = new ArrayList<>();
        Map<String, Map<Long, SuggestResult>> entityResultsMap = new HashMap<>();

        topHitsPerEntity.getDocumentsPerEntity().forEach((key, documents) -> {
            AutoSuggestData dataPerEntity = new AutoSuggestData();

            if (!entityResultsMap.containsKey(key)) {
                entityResultsMap.put(key.getKey(), new HashMap<>());
            }
            List<SuggestResult> responseResultDocList =
                    convertEsDataToResponse(key.getKey(), documents, entityResultsMap);
            dataPerEntity.setEntityType(key.getKey());
            dataPerEntity.setResults(responseResultDocList);
            suggestData.add(dataPerEntity);
        });

        AutoSuggestResponse autosuggestResponse = new AutoSuggestResponse();
        autosuggestResponse.setData(suggestData);
        autosuggestResponse.setPerEntitySuggestMap(entityResultsMap);

        return autosuggestResponse;
    }

    private AutoSuggestResponse extractDataFromSearchResults(EducationEntity entity,
            ElasticResponse<AutoSuggestEsData> esResponse) {

        List<AutoSuggestEsData> documents = esResponse.getDocuments();

        Map<String, Map<Long, SuggestResult>> entityResultsMap = new HashMap<>();
        entityResultsMap.put(entity.name().toLowerCase(), new HashMap<>());

        List<SuggestResult> responseDocList =
                convertEsDataToResponse(entity.name().toLowerCase(), documents, entityResultsMap);


        AutoSuggestData dataPerEntity = new AutoSuggestData();
        dataPerEntity.setEntityType(entity.name().toLowerCase());
        dataPerEntity.setResults(responseDocList);

        List<AutoSuggestData> suggestData = new ArrayList<>();
        suggestData.add(dataPerEntity);

        AutoSuggestResponse autosuggestResponse = new AutoSuggestResponse();
        autosuggestResponse.setData(suggestData);
        autosuggestResponse.setPerEntitySuggestMap(entityResultsMap);

        return autosuggestResponse;
    }


    private void groupEntityBasedOnActions(AutoSuggestResponse autoSuggestResponse,
            List<UserAction> actions,
            Long userId) {
        if (!CollectionUtils.isEmpty(actions) && actions.contains(UserAction.SHORTLIST)
                && Objects.nonNull(userId) && userId > 0
                && !CollectionUtils.isEmpty(autoSuggestResponse.getPerEntitySuggestMap())) {
            Map<String, Map<Long, SuggestResult>> entitySuggestMap =
                    autoSuggestResponse.getPerEntitySuggestMap();
            for (String entity : entitySuggestMap.keySet()) {
                Set<Long> entityIds = entitySuggestMap.get(entity).keySet();
                if (!CollectionUtils.isEmpty(entityIds)) {
                    List<Long> subscribedEntities = subscriptionDetailHelper
                            .getSubscribedEntities(EducationEntity.getEntityFromValue(entity),
                                    userId, entityIds);
                    if (!CollectionUtils.isEmpty(subscribedEntities)) {
                        List<SuggestResult> shortListData = new ArrayList<>();
                        for (Long entityId : subscribedEntities) {
                            shortListData.add(entitySuggestMap.get(entity).get(entityId));
                        }
                        for (AutoSuggestData data : autoSuggestResponse.getData()) {
                            if (data.getEntityType().equals(entity)) {
                                List<SuggestResult> resultLists = new ArrayList<>();
                                for (SuggestResult result : data.getResults()) {
                                    if (!subscribedEntities.contains(result.getEntityId())) {
                                        resultLists.add(result);
                                    }
                                }
                                data.setResults(resultLists);
                            }
                        }
                        AutoSuggestData autoSuggestData = new AutoSuggestData();
                        autoSuggestData.setEntityType(UserAction.SHORTLIST.name().toLowerCase());
                        autoSuggestData.setResults(shortListData);
                        autoSuggestResponse.getData().add(0, autoSuggestData);
                    }
                }
            }
            autoSuggestResponse.getPerEntitySuggestMap().clear();
        }
    }
}
