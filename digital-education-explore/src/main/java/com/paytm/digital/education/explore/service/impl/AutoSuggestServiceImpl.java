package com.paytm.digital.education.explore.service.impl;

import static com.paytm.digital.education.explore.constants.ExploreConstants.AUTOSUGGEST_ANALYZER;
import static com.paytm.digital.education.explore.constants.ExploreConstants.AUTOSUGGEST_INDEX;
import static com.paytm.digital.education.explore.constants.ExploreConstants.AUTOSUGGEST_NAMES;
import static com.paytm.digital.education.explore.constants.ExploreConstants.BLANK;
import static com.paytm.digital.education.explore.constants.ExploreConstants.DEFAULT_AUTOSUGGEST_COMPARE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.DEFAULT_AUTOSUGGEST_SIZE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.DEFAULT_OFFSET;
import static com.paytm.digital.education.explore.constants.ExploreConstants.ENTITY_TYPE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_CLASS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.MINUS_TEN;
import static com.paytm.digital.education.explore.constants.ExploreConstants.OFFICIAL_NAME;
import static com.paytm.digital.education.explore.constants.ExploreConstants.OTHER;
import static com.paytm.digital.education.explore.constants.ExploreConstants.RANKING_OVERALL;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SIXTY;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SUMMARY;
import static com.paytm.digital.education.explore.constants.ExploreConstants.ZERO;
import static com.paytm.digital.education.explore.enums.EducationEntity.INSTITUTE;

import com.paytm.digital.education.elasticsearch.enums.AggregationType;
import com.paytm.digital.education.elasticsearch.enums.DataSortOrder;
import com.paytm.digital.education.elasticsearch.enums.FilterQueryType;
import com.paytm.digital.education.elasticsearch.models.AggregateField;
import com.paytm.digital.education.elasticsearch.models.AggregationResponse;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.elasticsearch.models.FilterField;
import com.paytm.digital.education.elasticsearch.models.SearchField;
import com.paytm.digital.education.elasticsearch.models.SortField;
import com.paytm.digital.education.elasticsearch.models.TopHitsAggregationResponse;
import com.paytm.digital.education.exception.EducationException;
import com.paytm.digital.education.explore.constants.ExploreConstants;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.enums.UserAction;
import com.paytm.digital.education.explore.es.model.AutoSuggestEsData;
import com.paytm.digital.education.explore.request.dto.search.SearchRequest;
import com.paytm.digital.education.explore.response.dto.suggest.AutoSuggestData;
import com.paytm.digital.education.explore.response.dto.suggest.AutoSuggestResponse;
import com.paytm.digital.education.explore.response.dto.suggest.SuggestResult;
import com.paytm.digital.education.explore.service.helper.ExamLogoHelper;
import com.paytm.digital.education.explore.service.helper.SubscriptionDetailHelper;
import com.paytm.digital.education.explore.utility.CommonUtil;
import com.paytm.digital.education.mapping.ErrorEnum;
import com.paytm.digital.education.search.service.AutoSuggestionService;
import com.paytm.digital.education.utility.HierarchyIdentifierUtils;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;


@Slf4j
@RequiredArgsConstructor
@Service
public class AutoSuggestServiceImpl {

    private final AutoSuggestionService    autoSuggestionService;
    private Map<String, String>      suggestClassLevelMap;
    private final SubscriptionDetailHelper subscriptionDetailHelper;
    private final ExamLogoHelper           examLogoHelper;
    private final SearchServiceImpl        searchServiceImpl;

    @Value("${autosuggest.single.entity.count}")
    private Integer singleEntityAutoSuggestCount;

    @PostConstruct
    private void generateLevelMap() {
        suggestClassLevelMap = HierarchyIdentifierUtils.getClassHierarchy(AutoSuggestEsData.class);
    }

    public AutoSuggestResponse getAll(List<EducationEntity> entities, boolean alphabeticalSorting,
            int limit) {
        AutoSuggestResponse autoSuggestResponse =
                getSuggestResults(null, entities, limit, alphabeticalSorting);
        return autoSuggestResponse;
    }

    public AutoSuggestResponse getSuggestions(String searchTerm, List<EducationEntity> entities,
            List<UserAction> actions, Long userId) {
        boolean isAlphabeticalSorting = false;
        int autoSuggestSize = DEFAULT_AUTOSUGGEST_SIZE;
        if (!CollectionUtils.isEmpty(actions) && actions.contains(UserAction.SHORTLIST)) {
            autoSuggestSize = DEFAULT_AUTOSUGGEST_COMPARE;
        } else if (!CollectionUtils.isEmpty(entities) && entities.size() == 1) {
            autoSuggestSize = singleEntityAutoSuggestCount;
        }
        AutoSuggestResponse autoSuggestResponse =
                getSuggestResults(searchTerm, entities, autoSuggestSize, isAlphabeticalSorting);
        groupEntityBasedOnActions(autoSuggestResponse, actions, userId);
        return autoSuggestResponse;
    }

    @Cacheable(value = "autosuggest")
    public AutoSuggestResponse getSuggestResults(String searchTerm, List<EducationEntity> entities,
            int size, boolean alphabeticalSorting) {
        ElasticRequest elasticRequest =
                buildAutoSuggestRequest(searchTerm, entities, size, alphabeticalSorting);
        ElasticResponse<AutoSuggestEsData> response = null;
        try {
            response = autoSuggestionService.suggest(elasticRequest, AutoSuggestEsData.class);
        } catch (TimeoutException | IOException ex) {
            log.error("Error caught while calling autosuggest service with exception : {}",
                    ex.getMessage());
            throw new EducationException(ErrorEnum.HTTP_REQUEST_FAILED, ex.getMessage(), null, ex);
        }
        return buildAutoSuggestResponse(elasticRequest, response, entities);
    }

    public AutoSuggestResponse autosuggestInstitute(String query, Integer limit) {
        AutoSuggestResponse autoSuggestResponse;

        if (Objects.isNull(query) || StringUtils.isBlank(query)) {
            autoSuggestResponse = getTopInstitutes(limit);
        } else {
            List<EducationEntity> entities = new ArrayList<>();
            entities.add(EducationEntity.INSTITUTE);
            autoSuggestResponse = getSuggestions(query, entities, null, null);
            if (Objects.nonNull(autoSuggestResponse) && CollectionUtils
                    .isEmpty(autoSuggestResponse.getData())) {
                addEmptyResponse(autoSuggestResponse);
            }
        }
        addDefaultOption(autoSuggestResponse);
        return autoSuggestResponse;
    }

    private void addEmptyResponse(AutoSuggestResponse autoSuggestResponse) {
        List<AutoSuggestData> asDataList = new ArrayList<>();
        AutoSuggestData asData = new AutoSuggestData();
        asData.setEntityType(INSTITUTE.name().toLowerCase());
        List<SuggestResult> suggestResults = new ArrayList<>();
        asData.setResults(suggestResults);
        asDataList.add(asData);
        autoSuggestResponse.setData(asDataList);
    }

    private void addDefaultOption(AutoSuggestResponse autoSuggestResponse) {
        List<AutoSuggestData> asDataList = autoSuggestResponse.getData();
        if (Objects.nonNull(asDataList)) {
            for (AutoSuggestData asData : asDataList) {
                if (asData.getEntityType().equalsIgnoreCase(INSTITUTE_CLASS)) {
                    List<SuggestResult> suggestResults = asData.getResults();
                    SuggestResult suggestResult = new SuggestResult(MINUS_TEN, OTHER);
                    String logo = CommonUtil
                            .getLogoLink(ExploreConstants.DUMMY_EXAM_ICON, EducationEntity.EXAM);
                    suggestResult.setLogo(logo);
                    suggestResults.add(suggestResult);
                }
            }
        }
    }

    public AutoSuggestResponse getTopInstitutes(Integer limit) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setEntity(EducationEntity.INSTITUTE);
        searchRequest.setFetchFilter(false);
        if (Objects.nonNull(limit) && limit > 1 && limit < SIXTY) {
            searchRequest.setLimit(limit - 1);
        } else {
            searchRequest.setLimit(SIXTY - 1);
        }
        searchRequest.setOffset(Integer.parseInt(ZERO));
        searchRequest.setFieldGroup(SUMMARY);
        searchRequest.setFilter(new HashMap<>());
        searchRequest.setTerm(BLANK);

        LinkedHashMap<String, DataSortOrder> map = new LinkedHashMap<>();
        map.put(RANKING_OVERALL, DataSortOrder.DESC);
        searchRequest.setSortOrder(map);
        AutoSuggestResponse autoSuggestResponse = new AutoSuggestResponse();
        try {
            autoSuggestResponse = searchServiceImpl.instituteSearch(searchRequest);
        } catch (Exception e) {
            log.error("Error in search response : {} ", e.getMessage());
        }
        return autoSuggestResponse;
    }

    private ElasticRequest buildAutoSuggestRequest(String term, List<EducationEntity> entities,
            int size, boolean alphabeticalSorting) {
        ElasticRequest elasticRequest = new ElasticRequest();
        elasticRequest.setIndex(AUTOSUGGEST_INDEX);
        elasticRequest.setAnalyzer(AUTOSUGGEST_ANALYZER);
        elasticRequest.setQueryTerm(term);
        elasticRequest.setOffSet(DEFAULT_OFFSET);
        elasticRequest.setLimit(size);
        String filterFieldPath = suggestClassLevelMap.get(ENTITY_TYPE);

        if (!CollectionUtils.isEmpty(entities)) {
            FilterField[] filterFields = new FilterField[1];
            filterFields[0] = new FilterField();
            filterFields[0].setName(ENTITY_TYPE);
            filterFields[0].setType(FilterQueryType.TERMS);
            List<String> values = entities.stream().map(item -> item.name().toLowerCase())
                    .collect(Collectors.toList());
            filterFields[0].setValues(values);
            filterFields[0].setPath(filterFieldPath);
            elasticRequest.setFilterFields(filterFields);
        }

        if (!CollectionUtils.isEmpty(entities) && entities.size() == 1) {
            elasticRequest.setSearchRequest(true);
        }

        if (CollectionUtils.isEmpty(entities)
                || (!CollectionUtils.isEmpty(entities) && entities.size() > 1)) {
            elasticRequest.setAggregationRequest(true);
            AggregateField[] aggFields = new AggregateField[1];
            aggFields[0] = new AggregateField();
            aggFields[0].setName(ENTITY_TYPE);
            aggFields[0].setType(AggregationType.TOP_HITS);
            if (alphabeticalSorting) {
                SortField[] sortFields = new SortField[1];
                sortFields[0] = new SortField();
                sortFields[0].setName(OFFICIAL_NAME);
                sortFields[0].setOrder(DataSortOrder.ASC);
                sortFields[0].setPath(suggestClassLevelMap.get(OFFICIAL_NAME));
                aggFields[0].setSortFields(sortFields);
            }
            elasticRequest.setAggregateFields(aggFields);
        }

        String searchFieldPath = suggestClassLevelMap.get(AUTOSUGGEST_NAMES);
        SearchField[] searchFields = new SearchField[1];
        searchFields[0] = new SearchField();
        searchFields[0].setName(AUTOSUGGEST_NAMES);
        searchFields[0].setPath(searchFieldPath);
        elasticRequest.setSearchFields(searchFields);

        return elasticRequest;
    }

    private AutoSuggestResponse buildAutoSuggestResponse(ElasticRequest elasticRequest,
            ElasticResponse<AutoSuggestEsData> esResponse, List<EducationEntity> entities) {
        if (elasticRequest.isSearchRequest()) {
            return getSearchResponse(esResponse, entities.get(0));
        }
        return getAggregationResponse(esResponse);
    }

    private AutoSuggestResponse getSearchResponse(ElasticResponse<AutoSuggestEsData> esResponse,
            EducationEntity entity) {
        AutoSuggestResponse response = new AutoSuggestResponse();
        List<AutoSuggestEsData> esDocumentList = esResponse.getDocuments();
        Map<Long, SuggestResult> suggestResultMap = new HashMap<>();
        List<AutoSuggestData> responseList = new ArrayList<>();
        AutoSuggestData autoSuggestData =
                getAutoSuggestResultData(entity.name().toLowerCase(), esDocumentList, suggestResultMap);
        responseList.add(autoSuggestData);
        List<AutoSuggestData> autoSuggestDataList = new ArrayList<>();
        autoSuggestDataList.add(autoSuggestData);
        response.setData(autoSuggestDataList);
        Map<String, Map<Long, SuggestResult>> entityResultMap =
                new HashMap<String, Map<Long, SuggestResult>>() {{
                    put(entity.name().toLowerCase(), suggestResultMap);
            }
        };
        response.setPerEntitySuggestMap(entityResultMap);
        return response;
    }

    private AutoSuggestResponse getAggregationResponse(
            ElasticResponse<AutoSuggestEsData> esResponse) {
        AutoSuggestResponse response = new AutoSuggestResponse();
        Map<String, AggregationResponse> aggregationResponse = esResponse.getAggregationResponse();
        if (aggregationResponse.containsKey(ENTITY_TYPE)) {
            TopHitsAggregationResponse<AutoSuggestEsData> topHitsPerEntity =
                    (TopHitsAggregationResponse<AutoSuggestEsData>) aggregationResponse
                            .get(ENTITY_TYPE);
            // topHitsPerEntity
            List<AutoSuggestData> suggestData = new ArrayList<>();
            Map<String, Map<Long, SuggestResult>> entityResultsMap = new HashMap<>();
            topHitsPerEntity.getDocumentsPerEntity().forEach((key, documents) -> {
                if (!entityResultsMap.containsKey(key)) {
                    entityResultsMap.put(key.getKey(), new HashMap<>());
                }
                AutoSuggestData autoSuggestData = getAutoSuggestResultData(key.getKey(), documents,
                        entityResultsMap.get(key.getKey()));
                suggestData.add(autoSuggestData);
            });
            response.setData(suggestData);
            response.setPerEntitySuggestMap(entityResultsMap);
        }
        return response;
    }

    private AutoSuggestData getAutoSuggestResultData(String entity,
            List<AutoSuggestEsData> documents, Map<Long, SuggestResult> resultMap) {
        AutoSuggestData autoSuggestData = new AutoSuggestData();
        List<SuggestResult> responseResultDocList = new ArrayList<>();
        documents.forEach(esDocument -> {
            SuggestResult responseDoc = getSuggestResult(esDocument);
            resultMap.put(responseDoc.getEntityId(), responseDoc);
            responseResultDocList.add(responseDoc);
        });
        autoSuggestData.setEntityType(entity);
        autoSuggestData.setResults(responseResultDocList);
        return autoSuggestData;
    }

    private SuggestResult getSuggestResult(AutoSuggestEsData esDocument) {
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
                        .getLogoLink(esDocument.getLogo(), esDocument.getEntityType()));
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
        return responseDoc;
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
