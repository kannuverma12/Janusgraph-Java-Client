package com.paytm.digital.education.coaching.consumer.service.search;

import com.paytm.digital.education.coaching.consumer.model.request.SearchRequest;
import com.paytm.digital.education.coaching.consumer.model.response.search.SearchResponse;
import com.paytm.digital.education.coaching.consumer.model.response.search.builder.CoachingSearchResponseBuilder;
import com.paytm.digital.education.coaching.es.model.CoachingCenterSearch;
import com.paytm.digital.education.coaching.es.model.CoachingCourseSearch;
import com.paytm.digital.education.coaching.es.model.CoachingInstituteSearch;
import com.paytm.digital.education.coaching.es.model.ExamSearch;
import com.paytm.digital.education.elasticsearch.models.AggregateField;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.elasticsearch.models.FilterField;
import com.paytm.digital.education.elasticsearch.models.Operator;
import com.paytm.digital.education.elasticsearch.models.SearchField;
import com.paytm.digital.education.elasticsearch.models.SortField;
import com.paytm.digital.education.enums.es.AggregationType;
import com.paytm.digital.education.enums.es.DataSortOrder;
import com.paytm.digital.education.enums.es.FilterQueryType;
import com.paytm.digital.education.exception.EducationException;
import com.paytm.digital.education.mapping.ErrorEnum;
import com.paytm.digital.education.search.service.ISearchService;
import com.paytm.digital.education.utility.CommonUtil;
import com.paytm.digital.education.utility.HierarchyIdentifierUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.paytm.digital.education.enums.es.FilterQueryType.RANGE;

@Service
@Slf4j
public abstract class AbstractSearchService {


    protected Map<Class, Map<String, String>> hierarchyMap;

    @Autowired
    private ISearchService searchService;

    @Autowired
    private CoachingSearchResponseBuilder coachingSearchResponseBuilder;

    @PostConstruct
    private void generateLevelMap() {
        hierarchyMap = new HashMap<>();
        hierarchyMap.put(CoachingCourseSearch.class, HierarchyIdentifierUtils.getClassHierarchy(
                CoachingCourseSearch.class));
        hierarchyMap.put(ExamSearch.class,
                HierarchyIdentifierUtils.getClassHierarchy(ExamSearch.class));
        hierarchyMap.put(CoachingInstituteSearch.class,
                HierarchyIdentifierUtils.getClassHierarchy(CoachingInstituteSearch.class));
        hierarchyMap.put(CoachingCenterSearch.class,
                HierarchyIdentifierUtils.getClassHierarchy(CoachingCenterSearch.class));
    }


    public abstract SearchResponse search(SearchRequest searchRequest)
            throws IOException, TimeoutException;

    protected void validateRequest(SearchRequest searchRequest,
            Map<String, FilterQueryType> filterQueryTypeMap) {
        if (!CollectionUtils.isEmpty(searchRequest.getFilter())) {
            List<String> invalidFilters = new ArrayList<>();
            searchRequest.getFilter().forEach((filterKeyName, values) -> {
                if (!filterQueryTypeMap.containsKey(filterKeyName)) {
                    invalidFilters.add(filterKeyName);
                }
            });
            if (!CollectionUtils.isEmpty(invalidFilters)) {
                throw new EducationException(ErrorEnum.FILTER_DOESNOT_EXIST,
                        "Applied filter is not present in filterQueryMap");
            }
        }
    }

    protected <T> void populateSearchFields(SearchRequest searchRequest,
            ElasticRequest elasticRequest, Map<String, Float> searchFieldKeys, Class<T> type) {
        if (StringUtils.isNotBlank(searchRequest.getTerm())) {
            SearchField[] searchFields = new SearchField[searchFieldKeys.size()];
            int i = 0;
            for (Map.Entry<String, Float> searchKey : searchFieldKeys.entrySet()) {
                SearchField searchField = new SearchField();
                searchField.setName(searchKey.getKey());
                searchField.setPath(hierarchyMap.get(type).get(searchKey.getKey()));
                searchField.setBoost(searchKey.getValue());
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
                    List<List<Double>> values =
                            ((List<List<Double>>) (Object) searchRequest.getFilter()
                                    .get(filterKey));
                    if (values.size() > 1) {
                        filterField.setOperator(Operator.OR);
                    } else {
                        filterField.setOperator(Operator.AND);
                    }
                    for (List<Double> value : values) {
                        if (CollectionUtils.isEmpty(value) || value.size() != 2) {
                            throw new EducationException(ErrorEnum.RANGE_TYPE_FILTER_VALUES_ERROR,
                                    "values of range filter must be of size 2", new String[] {"2"});
                        }
                    }
                }
                filterField.setValues(searchRequest.getFilter().get(filterKey));
                filterFields[i++] = filterField;
            }
            elasticRequest.setFilterFields(filterFields);
        }
    }

    protected <T> void populateAggregateFields(SearchRequest searchRequest,
            ElasticRequest elasticRequest, AggregateField[] aggregateFields, Class<T> type) {
        if (searchRequest.getFetchFilter()) {
            Map<String, List<Object>> filters = searchRequest.getFilter();
            for (int i = 0; i < aggregateFields.length; i++) {
                aggregateFields[i].setPath(
                        hierarchyMap.get(type).get(aggregateFields[i].getName()));
                if (AggregationType.TERMS.equals(aggregateFields[i].getType())) {
                    if (!CollectionUtils.isEmpty(filters)) {
                        addFilters(filters, aggregateFields[i]);
                    }
                } else if (AggregationType.TOP_HITS.equals(aggregateFields[i].getType())) {
                    if (!CollectionUtils.isEmpty(searchRequest.getSortOrder())) {
                        addSortOrder(searchRequest.getSortOrder(), aggregateFields[i], type);
                    }
                }

            }
            elasticRequest.setAggregateFields(aggregateFields);
        }

    }

    private void addFilters(Map<String, List<Object>> filters, AggregateField aggregateField) {
        if (!CollectionUtils.isEmpty(filters.get(aggregateField.getName()))) {
            // TODO: need a sol, as ES include exclude takes only long[] and String[]
            String[] valuesString =
                    filters.get(aggregateField.getName()).toArray(new String[] {});
            aggregateField.setValues(valuesString);
        }
    }

    private <T> void addSortOrder(Map<String, DataSortOrder> sortOrderMap,
            AggregateField aggregateField, Class<T> type) {
        SortField[] sortFields = new SortField[sortOrderMap.size()];
        int i = 0;
        for (Map.Entry<String, DataSortOrder> key : sortOrderMap.entrySet()) {
            SortField sortField = new SortField();
            sortField.setName(key.getKey());
            sortField.setPath(hierarchyMap.get(type).get(key.getKey()));
            sortField.setOrder(key.getValue());
            sortFields[i++] = sortField;
        }
        aggregateField.setSortFields(sortFields);
    }


    protected abstract ElasticRequest buildSearchRequest(SearchRequest searchRequest);

    /**
     * If search request sort orders are null then we'll use BE sort orders
     */
    protected <T> void populateSortFields(SearchRequest searchRequest,
            ElasticRequest elasticRequest, Class<T> type) {
        if (!CollectionUtils.isEmpty(searchRequest.getSortOrder())) {
            SortField[] sortFields = new SortField[searchRequest.getSortOrder().size()];
            int i = 0;
            for (Map.Entry<String, DataSortOrder> key : searchRequest.getSortOrder().entrySet()) {
                SortField sortField = new SortField();
                sortField.setName(key.getKey());
                sortField.setPath(hierarchyMap.get(type).get(key.getKey()));
                sortField.setOrder(key.getValue());
                sortFields[i++] = sortField;
            }
            elasticRequest.setSortFields(sortFields);
        }
    }

    protected void buildSearchResponse(SearchResponse searchResponse,
            ElasticResponse elasticResponse, ElasticRequest elasticRequest) {
        if (elasticRequest.isSearchRequest()) {
            Map<String, Map<String, Object>> propertyMap = null;
            populateSearchResults(searchResponse, elasticResponse, propertyMap, elasticRequest);
            long total = elasticResponse.getTotalSearchResultsCount();
            searchResponse.setTotal(total);
        }
        if (elasticRequest.isAggregationRequest()) {
            Map<String, Map<String, Object>> propertyMap = null;
            coachingSearchResponseBuilder
                    .populateSearchFilters(searchResponse, elasticResponse, elasticRequest,
                            propertyMap);
        }
    }

    protected ElasticResponse initiateSearch(ElasticRequest elasticRequest, Class type)
            throws IOException, TimeoutException {
        return searchService.search(elasticRequest, type);
    }

    protected ElasticRequest createSearchRequest(SearchRequest searchRequest, String analyzer,
            String index) {
        CommonUtil.convertStringValuesToLowerCase(searchRequest.getFilter());
        ElasticRequest elasticRequest = new ElasticRequest();
        elasticRequest.setQueryTerm(searchRequest.getTerm());
        elasticRequest.setIndex(index);
        elasticRequest.setAnalyzer(analyzer);
        if (searchRequest.getFetchSearchResults()) {
            elasticRequest.setSearchRequest(true);
        }
        if (searchRequest.getFetchFilter()) {
            elasticRequest.setAggregationRequest(true);
        }
        elasticRequest.setLimit(searchRequest.getLimit());
        elasticRequest.setOffSet(searchRequest.getOffset());
        return elasticRequest;
    }

    protected abstract void populateSearchResults(SearchResponse searchResponse,
            ElasticResponse elasticResponse, Map<String, Map<String, Object>> properties,
            ElasticRequest elasticRequest);

}
