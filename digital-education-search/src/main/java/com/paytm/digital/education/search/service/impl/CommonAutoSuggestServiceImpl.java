package com.paytm.digital.education.search.service.impl;

import com.paytm.digital.education.elasticsearch.models.AggregateField;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.elasticsearch.models.FilterField;
import com.paytm.digital.education.elasticsearch.models.SearchField;
import com.paytm.digital.education.elasticsearch.models.SortField;
import com.paytm.digital.education.elasticsearch.service.ElasticSearchService;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.enums.es.AggregationType;
import com.paytm.digital.education.enums.es.DataSortOrder;
import com.paytm.digital.education.enums.es.FilterQueryType;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.mapping.ErrorEnum;
import com.paytm.digital.education.search.model.AutoSuggestEsData;
import com.paytm.digital.education.search.service.CommonAutoSuggestionService;
import com.paytm.digital.education.utility.HierarchyIdentifierUtils;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static com.paytm.digital.education.constant.CommonConstants.AUTOSUGGEST_ANALYZER;
import static com.paytm.digital.education.constant.CommonConstants.AUTOSUGGEST_INDEX;
import static com.paytm.digital.education.constant.CommonConstants.AUTOSUGGEST_NAMES;
import static com.paytm.digital.education.constant.CommonConstants.DEFAULT_OFFSET;
import static com.paytm.digital.education.constant.CommonConstants.ENTITY_TYPE;
import static com.paytm.digital.education.constant.CommonConstants.ENTITY_TYPE_CITY;
import static com.paytm.digital.education.constant.CommonConstants.ENTITY_TYPE_STATE;
import static com.paytm.digital.education.constant.CommonConstants.OFFICIAL_NAME;

@Service
@AllArgsConstructor
public class CommonAutoSuggestServiceImpl implements CommonAutoSuggestionService {

    private static final Logger log = LoggerFactory.getLogger(CommonAutoSuggestServiceImpl.class);

    private ElasticSearchService elasticSearchService;
    private Map<String, String>  suggestClassLevelMap;

    @PostConstruct
    private void generateLevelMap() {
        suggestClassLevelMap = HierarchyIdentifierUtils.getClassHierarchy(AutoSuggestEsData.class);
    }

    public ElasticResponse<AutoSuggestEsData> suggest(String searchTerm,
            List<EducationEntity> entities, int size) {
        ElasticRequest elasticRequest = buildAutoSuggestRequest(searchTerm, entities, size);
        ElasticResponse<AutoSuggestEsData> response = null;
        try {
            return elasticSearchService.executeSearch(elasticRequest, AutoSuggestEsData.class);
        } catch (TimeoutException | IOException ex) {
            log.error("Error caught while calling autosuggestion service with exception : ", ex);
            throw new RuntimeException(ex.getMessage());
        }
    }

    private ElasticRequest buildAutoSuggestRequest(String term, List<EducationEntity> entities,
            int size) {
        if (CollectionUtils.isEmpty(entities)) {
            throw new BadRequestException(ErrorEnum.ENTITY_LIST_EMPTY,
                    ErrorEnum.ENTITY_LIST_EMPTY.getExternalMessage());
        }
        ElasticRequest elasticRequest = new ElasticRequest();
        setConfigurationsInRequest(elasticRequest);
        elasticRequest.setQueryTerm(term);
        elasticRequest.setLimit(size);
        boolean alphabeticalSorting = true;
        List<String> entityValues = entities.stream().map(item -> item.name().toLowerCase())
                .collect(Collectors.toList());
        FilterField[] filterFields = buildFilterFields(entityValues);
        elasticRequest.setFilterFields(filterFields);
        /**
         * Alphabetical sort is applied for requests containing only state or city entity
         * otherwise sort order is relevance(default).
         */
        for (String value : entityValues) {
            if (!value.equalsIgnoreCase(ENTITY_TYPE_CITY)
                    && !value.equalsIgnoreCase(ENTITY_TYPE_STATE)) {
                alphabeticalSorting = false;
            }
        }
        /**
         * Top Hits aggregation is not required for request containing single entity
         * */
        if (entityValues.size() == 1) {
            elasticRequest.setSearchRequest(true);
            if (alphabeticalSorting) {
                SortField[] sortFields = buildSortFields();
                elasticRequest.setSortFields(sortFields);
            }
        } else {
            elasticRequest.setAggregationRequest(true);
            AggregateField[] aggFields =
                    buildAggregateFields(alphabeticalSorting);
            elasticRequest.setAggregateFields(aggFields);
        }

        SearchField[] searchFields = getSearchFields();
        elasticRequest.setSearchFields(searchFields);

        return elasticRequest;
    }

    private void setConfigurationsInRequest(ElasticRequest elasticRequest) {
        elasticRequest.setIndex(AUTOSUGGEST_INDEX);
        elasticRequest.setAnalyzer(AUTOSUGGEST_ANALYZER);
        elasticRequest.setOffSet(DEFAULT_OFFSET);
    }

    private FilterField[] buildFilterFields(List<String> values) {
        FilterField[] filterFields = new FilterField[1];
        filterFields[0] = new FilterField();
        filterFields[0].setName(ENTITY_TYPE);
        filterFields[0].setType(FilterQueryType.TERMS);
        filterFields[0].setValues(values);
        filterFields[0].setPath(suggestClassLevelMap.get(ENTITY_TYPE));
        return filterFields;
    }

    private AggregateField[] buildAggregateFields(boolean alphabeticalSorting) {
        AggregateField[] aggFields = new AggregateField[1];
        aggFields[0] = new AggregateField();
        aggFields[0].setName(ENTITY_TYPE);
        aggFields[0].setType(AggregationType.TOP_HITS);
        if (alphabeticalSorting) {
            SortField[] sortFields = buildSortFields();
            aggFields[0].setSortFields(sortFields);
        }
        return aggFields;
    }

    private SortField[] buildSortFields() {
        SortField[] sortFields = new SortField[1];
        sortFields[0] = new SortField();
        sortFields[0].setName(OFFICIAL_NAME);
        sortFields[0].setOrder(DataSortOrder.ASC);
        sortFields[0].setPath(suggestClassLevelMap.get(OFFICIAL_NAME));
        return sortFields;
    }

    private SearchField[] getSearchFields() {
        SearchField[] searchFields = new SearchField[1];
        searchFields[0] = new SearchField();
        searchFields[0].setName(AUTOSUGGEST_NAMES);
        searchFields[0].setPath(suggestClassLevelMap.get(AUTOSUGGEST_NAMES));
        return searchFields;
    }

}
