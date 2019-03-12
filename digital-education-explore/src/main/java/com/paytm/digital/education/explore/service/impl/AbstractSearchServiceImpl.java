package com.paytm.digital.education.explore.service.impl;

import static com.paytm.digital.education.elasticsearch.enums.FilterQueryType.RANGE;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import com.paytm.digital.education.elasticsearch.enums.DataSortOrder;
import com.paytm.digital.education.elasticsearch.enums.FilterQueryType;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.elasticsearch.models.FilterField;
import com.paytm.digital.education.elasticsearch.models.SearchField;
import com.paytm.digital.education.elasticsearch.models.SortField;
import com.paytm.digital.education.exception.EducationException;
import com.paytm.digital.education.explore.es.model.CourseSearch;
import com.paytm.digital.education.explore.es.model.ExamSearch;
import com.paytm.digital.education.explore.es.model.InstituteSearch;
import com.paytm.digital.education.explore.request.dto.search.SearchRequest;
import com.paytm.digital.education.explore.response.builders.SearchResponseBuilder;
import com.paytm.digital.education.explore.response.dto.search.SearchResponse;
import com.paytm.digital.education.mapping.ErrorEnum;
import com.paytm.digital.education.search.service.ISearchService;
import com.paytm.digital.education.utility.HierarchyIdentifierUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public abstract class AbstractSearchServiceImpl {

    @Autowired
    private ISearchService                    searchService;

    @Autowired
    private SearchResponseBuilder             searchResponseBuilder;

    protected Map<Class, Map<String, String>> hierarchyMap;

    @PostConstruct
    private void generateLevelMap() {
        hierarchyMap = new HashMap<>();
        hierarchyMap.put(InstituteSearch.class, HierarchyIdentifierUtils.getClassHierarchy(
                InstituteSearch.class));
        hierarchyMap.put(ExamSearch.class,
                HierarchyIdentifierUtils.getClassHierarchy(ExamSearch.class));
        hierarchyMap.put(CourseSearch.class,
                HierarchyIdentifierUtils.getClassHierarchy(CourseSearch.class));
    }


    public abstract SearchResponse search(SearchRequest searchRequest)
            throws IOException, TimeoutException;

    protected void validateRequest(SearchRequest searchRequest,
            Map<String, FilterQueryType> filterQueryTypeMap) {
        searchRequest.getFilter().forEach((filterKeyName, values) -> {
            if (!filterQueryTypeMap.containsKey(filterKeyName)) {
                throw new EducationException(ErrorEnum.FILTER_DOESNOT_EXIST,
                        "Applied filter is not present in filterQueryMap");
            }
        });
    }

    protected <T> void populateSearchFields(SearchRequest searchRequest,
            ElasticRequest elasticRequest, List<String> searchFieldKeys, Class<T> type) {
        if (StringUtils.isNotBlank(searchRequest.getTerm())) {
            SearchField[] searchFields = new SearchField[searchFieldKeys.size()];
            int i = 0;
            for (String searchKey : searchFieldKeys) {
                SearchField searchField = new SearchField();
                searchField.setName(searchKey);
                searchField.setPath(hierarchyMap.get(type).get(searchKey));
                searchField.setBoost(1.0f);
                searchFields[i++] = searchField;
            }
            elasticRequest.setSearchFields(searchFields);
        }
    }

    protected <T> void populateFilterFields(SearchRequest searchRequest,
            ElasticRequest elasticRequest, Class<T> type,
            Map<String, FilterQueryType> filterQueryTypeMap) {
        if (!CollectionUtils.isEmpty(searchRequest.getFilter())) {
            int filterSize = searchRequest.getFilter().size();
            FilterField[] filterFields = new FilterField[filterSize];
            int i = 0;
            for (String filterKey : searchRequest.getFilter().keySet()) {
                FilterField filterField = new FilterField();
                filterField.setName(filterKey);
                filterField.setPath(hierarchyMap.get(type).get(filterKey));
                filterField.setType(filterQueryTypeMap.get(filterKey));
                if (filterQueryTypeMap.get(filterKey).equals(RANGE)) {
                    List<Object> values = searchRequest.getFilter().get(filterKey);
                    if (CollectionUtils.isEmpty(values) || values.size() < 2) {
                        throw new EducationException(ErrorEnum.RANGE_TYPE_FILTER_VALUES_ERROR,
                                "values of range filter must be of size 2", new String[] {"2"});
                    }
                }
                filterField.setValues(searchRequest.getFilter().get(filterKey));
                filterFields[i++] = filterField;
            }
            elasticRequest.setFilterFields(filterFields);
        }
    }

    protected abstract void populateAggregateFields(SearchRequest searchRequest,
            ElasticRequest elasticRequest);

    protected abstract ElasticRequest buildSearchRequest(SearchRequest searchRequest);

    protected <T> void populateSortFields(SearchRequest searchRequest,
            ElasticRequest elasticRequest, Class<T> type, List<String> sortKeysInOrder) {
        if (!CollectionUtils.isEmpty(sortKeysInOrder)) {
            SortField[] sortFields = new SortField[sortKeysInOrder.size()];
            int i = 0;
            for (String keyName : sortKeysInOrder) {
                SortField sortField = new SortField();
                sortField.setName(keyName);
                sortField.setPath(hierarchyMap.get(type).get(keyName));
                sortField.setOrder(DataSortOrder.ASC);
                sortFields[i++] = sortField;
            }
            elasticRequest.setSortFields(sortFields);
        }
    }

    protected SearchResponse buildSearchResponse(
            ElasticResponse elasticResponse,
            ElasticRequest elasticRequest) {
        SearchResponse searchResponse = new SearchResponse(elasticRequest.getQueryTerm());
        if (elasticRequest.isSearchRequest()) {
            populateSearchResults(searchResponse, elasticResponse);
            long total = elasticResponse.getTotalSearchResultsCount();
            searchResponse.setTotal(total);
        }
        if (elasticRequest.isAggregationRequest()) {
            searchResponseBuilder
                    .populateSearchFilters(searchResponse, elasticResponse, elasticRequest);
        }
        return searchResponse;
    }

    protected ElasticResponse initiateSearch(ElasticRequest elasticRequest, Class type)
            throws IOException, TimeoutException {
        return searchService.search(elasticRequest, type);
    }

    protected ElasticRequest createSearchRequest(SearchRequest searchRequest, String analyzer,
            String index) {
        ElasticRequest elasticRequest = new ElasticRequest();
        elasticRequest.setQueryTerm(searchRequest.getTerm());
        elasticRequest.setIndex(index);
        elasticRequest.setAnalyzer(analyzer);
        elasticRequest.setSearchRequest(true);
        if (searchRequest.getFetchFilter()) {
            elasticRequest.setAggregationRequest(true);
        }
        elasticRequest.setLimit(searchRequest.getLimit());
        elasticRequest.setOffSet(searchRequest.getOffset());
        return elasticRequest;
    }

    protected abstract void populateSearchResults(SearchResponse searchResponse,
            ElasticResponse elasticResponse);

}
