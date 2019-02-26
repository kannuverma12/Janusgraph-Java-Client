package com.paytm.digital.education.explore.service.impl;

import static com.paytm.digital.education.explore.constants.ExploreConstants.AUTOSUGGEST_ANALYZER;
import static com.paytm.digital.education.explore.constants.ExploreConstants.AUTOSUGGEST_INDEX;
import static com.paytm.digital.education.explore.constants.ExploreConstants.AUTOSUGGEST_NAMES;
import static com.paytm.digital.education.explore.constants.ExploreConstants.DEFAULT_OFFSET;
import static com.paytm.digital.education.explore.constants.ExploreConstants.DEFAULT_SIZE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.ENTITY_TYPE;

import com.paytm.digital.education.elasticsearch.enums.DataSortOrder;
import com.paytm.digital.education.elasticsearch.enums.FilterQueryType;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.FilterField;
import com.paytm.digital.education.elasticsearch.models.SearchField;
import com.paytm.digital.education.elasticsearch.models.SortField;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.es.model.AutoSuggestEsData;
import com.paytm.digital.education.explore.response.dto.suggest.AutoSuggestData;
import com.paytm.digital.education.explore.response.dto.suggest.AutoSuggestResponse;
import com.paytm.digital.education.explore.response.dto.suggest.SuggestResult;
import com.paytm.digital.education.search.service.AutoSuggestionService;
import com.paytm.digital.education.utility.HierarchyIdentifierUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

    public AutoSuggestResponse getSuggestions(String searchTerm, List<EducationEntity> entities) {

        ElasticRequest elasticRequest = buildAutoSuggestRequest(searchTerm, entities);
        List<AutoSuggestEsData> autoSuggestEsDataList = null;
        try {
            autoSuggestEsDataList =
                    autoSuggestionService.suggest(elasticRequest, AutoSuggestEsData.class);
        } catch (TimeoutException | IOException ex) {
            log.error("Error caught while calling autosuggest service with exception : {}", ex);
            throw new RuntimeException(ex.getMessage());
        }

        return buildAutoSuggestResponse(autoSuggestEsDataList, searchTerm);
    }

    private ElasticRequest buildAutoSuggestRequest(String term, List<EducationEntity> entities) {
        ElasticRequest elasticRequest = new ElasticRequest();
        elasticRequest.setIndex(AUTOSUGGEST_INDEX);
        elasticRequest.setAnalyzer(AUTOSUGGEST_ANALYZER);
        elasticRequest.setQueryTerm(term);
        elasticRequest.setOffSet(DEFAULT_OFFSET);
        elasticRequest.setLimit(DEFAULT_SIZE);
        elasticRequest.setSearchRequest(true);
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

        String searchFieldPath = suggestClassLevelMap.get(AUTOSUGGEST_NAMES);
        SearchField[] searchFields = new SearchField[1];
        searchFields[0] = new SearchField();
        searchFields[0].setName(AUTOSUGGEST_NAMES);
        searchFields[0].setPath(searchFieldPath);
        elasticRequest.setSearchFields(searchFields);

        SortField[] sortFields = new SortField[1];
        sortFields[0] = new SortField();
        sortFields[0].setName("_score");
        sortFields[0].setOrder(DataSortOrder.DESC);
        elasticRequest.setSortFields(sortFields);

        return elasticRequest;
    }

    private AutoSuggestResponse buildAutoSuggestResponse(
            List<AutoSuggestEsData> autoSuggestEsDataList, String searchTerm) {
        AutoSuggestResponse autoSuggestResponse = new AutoSuggestResponse(searchTerm);
        if (CollectionUtils.isEmpty(autoSuggestEsDataList)) {
            autoSuggestResponse.setData(new ArrayList<>());
            return autoSuggestResponse;
        }

        Map<EducationEntity, List<SuggestResult>> bucketData = new HashMap<>();
        for (AutoSuggestEsData autoSuggestEsData : autoSuggestEsDataList) {
            if (!bucketData.containsKey(autoSuggestEsData.getEntityType())) {
                bucketData.put(autoSuggestEsData.getEntityType(), new ArrayList<>());
            }
            bucketData.get(autoSuggestEsData.getEntityType())
                    .add(new SuggestResult(autoSuggestEsData.getEntityId(),
                            autoSuggestEsData.getOfficialName()));
        }
        List<AutoSuggestData> autoSuggestDataList = new ArrayList<>();
        for (EducationEntity entityType : bucketData.keySet()) {
            AutoSuggestData autoSuggestData = new AutoSuggestData();
            autoSuggestData.setEntityType(entityType.name());
            autoSuggestData.setResults(bucketData.get(entityType));
            autoSuggestDataList.add(autoSuggestData);
        }
        autoSuggestResponse.setData(autoSuggestDataList);

        return autoSuggestResponse;
    }

}
