package com.paytm.digital.education.explore.service.impl;

import static com.paytm.digital.education.explore.constants.ExploreConstants.AUTOSUGGEST_ANALYZER;
import static com.paytm.digital.education.explore.constants.ExploreConstants.AUTOSUGGEST_INDEX;
import static com.paytm.digital.education.explore.constants.ExploreConstants.AUTOSUGGEST_NAMES;
import static com.paytm.digital.education.explore.constants.ExploreConstants.DEFAULT_AUTOSUGGEST_SIZE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.DEFAULT_OFFSET;
import static com.paytm.digital.education.explore.constants.ExploreConstants.OFFICIAL_NAME;
import static com.paytm.digital.education.explore.constants.ExploreConstants.ENTITY_TYPE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.ENTITY_TYPE_CITY;
import static com.paytm.digital.education.explore.constants.ExploreConstants.ENTITY_TYPE_STATE;

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
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.es.model.AutoSuggestEsData;
import com.paytm.digital.education.explore.response.dto.suggest.AutoSuggestData;
import com.paytm.digital.education.explore.response.dto.suggest.AutoSuggestResponse;
import com.paytm.digital.education.explore.response.dto.suggest.SuggestResult;
import com.paytm.digital.education.search.service.AutoSuggestionService;
import com.paytm.digital.education.utility.HierarchyIdentifierUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;

@Slf4j
@AllArgsConstructor
@Service
public class AutoSuggestServiceImpl {

    private AutoSuggestionService autoSuggestionService;
    private Map<String, String>   suggestClassLevelMap;

    @PostConstruct
    private void generateLevelMap() {
        suggestClassLevelMap = HierarchyIdentifierUtils.getClassHierarchy(AutoSuggestEsData.class);
    }

    @Cacheable(value = "autosuggest")
    public AutoSuggestResponse getSuggestions(String searchTerm, List<EducationEntity> entities) {

        ElasticRequest elasticRequest = buildAutoSuggestRequest(searchTerm, entities);
        ElasticResponse<AutoSuggestEsData> response = null;
        try {
            response =
                    (ElasticResponse<AutoSuggestEsData>) autoSuggestionService
                            .suggest(elasticRequest, AutoSuggestEsData.class);
        } catch (TimeoutException | IOException ex) {
            log.error("Error caught while calling autosuggest service with exception : {}", ex);
            throw new RuntimeException(ex.getMessage());
        }

        return buildAutoSuggestResponse(response);
    }

    private ElasticRequest buildAutoSuggestRequest(String term, List<EducationEntity> entities) {
        ElasticRequest elasticRequest = new ElasticRequest();
        elasticRequest.setIndex(AUTOSUGGEST_INDEX);
        elasticRequest.setAnalyzer(AUTOSUGGEST_ANALYZER);
        elasticRequest.setQueryTerm(term);
        elasticRequest.setOffSet(DEFAULT_OFFSET);
        elasticRequest.setLimit(DEFAULT_AUTOSUGGEST_SIZE);
        elasticRequest.setAggregationRequest(true);
        String filterFieldPath = suggestClassLevelMap.get(ENTITY_TYPE);
        boolean alphabeticalSorting = true;

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
            /**
             * Alphabetical sort is applied for requests containing only state or city entity
             * otherwise sort order is relevance(default).
             */
            for (String value : values) {
                if (!value.equalsIgnoreCase(ENTITY_TYPE_CITY)
                        && !value.equalsIgnoreCase(ENTITY_TYPE_STATE)) {
                    alphabeticalSorting = false;
                }
            }
        }

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

        String searchFieldPath = suggestClassLevelMap.get(AUTOSUGGEST_NAMES);
        SearchField[] searchFields = new SearchField[1];
        searchFields[0] = new SearchField();
        searchFields[0].setName(AUTOSUGGEST_NAMES);
        searchFields[0].setPath(searchFieldPath);
        elasticRequest.setSearchFields(searchFields);

        return elasticRequest;
    }

    private AutoSuggestResponse buildAutoSuggestResponse(
            ElasticResponse<AutoSuggestEsData> esResponse) {

        AutoSuggestResponse response = new AutoSuggestResponse();

        Map<String, AggregationResponse> aggregationResponse = esResponse.getAggregationResponse();

        if (aggregationResponse.containsKey(ENTITY_TYPE)) {

            TopHitsAggregationResponse<AutoSuggestEsData> topHitsPerEntity =
                    (TopHitsAggregationResponse<AutoSuggestEsData>) aggregationResponse
                            .get(ENTITY_TYPE);

            // topHitsPerEntity
            List<AutoSuggestData> suggestData = new ArrayList<>();

            topHitsPerEntity.getDocumentsPerEntity().forEach((key, documents) -> {
                AutoSuggestData dataPerEntity = new AutoSuggestData();
                List<SuggestResult> responseResultDocList = new ArrayList<>();
                documents.forEach(esDocument -> {
                    SuggestResult responseDoc = new SuggestResult(esDocument.getEntityId(),
                            esDocument.getOfficialName());
                    responseResultDocList.add(responseDoc);
                });
                dataPerEntity.setEntityType(key.getKey());
                dataPerEntity.setResults(responseResultDocList);
                suggestData.add(dataPerEntity);
            });
            response.setData(suggestData);
        }
        return response;
    }

}
