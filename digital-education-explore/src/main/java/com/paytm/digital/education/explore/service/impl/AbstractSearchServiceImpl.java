package com.paytm.digital.education.explore.service.impl;

import static com.paytm.digital.education.elasticsearch.enums.FilterQueryType.RANGE;
import static com.paytm.digital.education.elasticsearch.enums.FilterQueryType.GEO_DISTANCE;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_SORT_FIELD;

import com.paytm.digital.education.elasticsearch.constants.ESConstants;
import com.paytm.digital.education.elasticsearch.enums.AggregationType;
import com.paytm.digital.education.elasticsearch.enums.DataSortOrder;
import com.paytm.digital.education.elasticsearch.enums.FilterQueryType;
import com.paytm.digital.education.elasticsearch.models.AggregateField;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.elasticsearch.models.FilterField;
import com.paytm.digital.education.elasticsearch.models.Operator;
import com.paytm.digital.education.elasticsearch.models.SearchField;
import com.paytm.digital.education.elasticsearch.models.SortField;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.exception.EducationException;
import com.paytm.digital.education.explore.enums.Client;
import com.paytm.digital.education.explore.es.model.ClassifierSearchDoc;
import com.paytm.digital.education.explore.es.model.CourseSearch;
import com.paytm.digital.education.explore.es.model.ExamSearch;
import com.paytm.digital.education.explore.es.model.GeoLocation;
import com.paytm.digital.education.explore.es.model.InstituteSearch;
import com.paytm.digital.education.explore.es.model.NestedCourseSearch;
import com.paytm.digital.education.explore.es.model.SchoolSearch;
import com.paytm.digital.education.explore.es.model.SearchHistoryEsDoc;
import com.paytm.digital.education.explore.request.dto.search.Classification;
import com.paytm.digital.education.explore.request.dto.search.SearchRequest;
import com.paytm.digital.education.explore.response.builders.SearchResponseBuilder;
import com.paytm.digital.education.explore.response.dto.search.ClassificationResponse;
import com.paytm.digital.education.explore.response.dto.search.SearchResponse;
import com.paytm.digital.education.explore.service.helper.CTAHelper;
import com.paytm.digital.education.explore.utility.CommonUtil;
import com.paytm.digital.education.mapping.ErrorEnum;
import com.paytm.digital.education.property.reader.PropertyReader;
import com.paytm.digital.education.search.service.ISearchService;
import com.paytm.digital.education.utility.HierarchyIdentifierUtils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import javax.annotation.PostConstruct;


@Component
public abstract class AbstractSearchServiceImpl {

    protected Map<Class, Map<String, String>> hierarchyMap;
    @Autowired
    private   ISearchService                  searchService;
    @Autowired
    private   SearchResponseBuilder           searchResponseBuilder;
    @Autowired
    private   PropertyReader                  propertyReader;

    @Autowired CTAHelper ctaHelper;

    @PostConstruct
    private void generateLevelMap() {
        hierarchyMap = new HashMap<>();
        hierarchyMap.put(InstituteSearch.class, HierarchyIdentifierUtils.getClassHierarchy(
                InstituteSearch.class));
        hierarchyMap.put(ExamSearch.class,
                HierarchyIdentifierUtils.getClassHierarchy(ExamSearch.class));
        hierarchyMap.put(NestedCourseSearch.class,
                HierarchyIdentifierUtils.getClassHierarchy(NestedCourseSearch.class));
        hierarchyMap.put(CourseSearch.class,
                HierarchyIdentifierUtils.getClassHierarchy(CourseSearch.class));
        hierarchyMap.put(ClassifierSearchDoc.class,
                HierarchyIdentifierUtils.getClassHierarchy(ClassifierSearchDoc.class));
        hierarchyMap.put(SearchHistoryEsDoc.class,
                HierarchyIdentifierUtils.getClassHierarchy(SearchHistoryEsDoc.class));
        hierarchyMap.put(SchoolSearch.class,
                HierarchyIdentifierUtils.getClassHierarchy(SchoolSearch.class));
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
        validateGeoLocationRequest(searchRequest);
    }

    private void validateGeoLocationRequest(SearchRequest searchRequest) {
        GeoLocation geoLocation = searchRequest.getGeoLocation();

        if (Objects.isNull(geoLocation)) {
            return;
        }

        if (Objects.isNull(geoLocation.getLat()) || Objects.isNull(geoLocation.getLon())) {
            throw new EducationException(ErrorEnum.LAT_OR_LON_MISSING,
                    "Latitude and longitude are mandatory in location.");
        }

        if (geoLocation.getLat() < -90 || geoLocation.getLat() > 90) {
            throw new EducationException(ErrorEnum.LAT_INVALID,
                    "Please provide valid latitude in request.");
        }

        if (geoLocation.getLon() < -180 || geoLocation.getLon() > 180) {
            throw new EducationException(ErrorEnum.LON_INVALID,
                    "Please provide valid longitude in request.");
        }

        if (Objects.nonNull(searchRequest.getRadius())
                && searchRequest.getRadius() > ESConstants.GEO_DISTANCE_FILTER_MAX_LIMIT_KMS) {
            throw new EducationException(ErrorEnum.GEO_DISTANCE_INVALID,
                    new Object[] {ESConstants.GEO_DISTANCE_FILTER_MAX_LIMIT_KMS});
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
                if (!CollectionUtils.isEmpty(filters)
                        && aggregateFields[i].getType() == AggregationType.TERMS
                        && filters.containsKey(aggregateFields[i].getName())) {
                    if (!CollectionUtils.isEmpty(filters.get(aggregateFields[i].getName()))) {
                        // TODO: need a sol, as ES include exclude takes only long[] and String[]
                        String[] valuesString =
                                filters.get(aggregateFields[i].getName()).toArray(new String[] {});
                        aggregateFields[i].setValues(valuesString);
                    }
                }
            }
            elasticRequest.setAggregateFields(aggregateFields);
        }

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

    protected void validateSortFields(SearchRequest searchRequest, Set<String> allowedSortFields) {
        if (!CollectionUtils.isEmpty(searchRequest.getSortOrder())) {
            searchRequest.getSortOrder().forEach((s, dataSortOrder) -> {
                if (!allowedSortFields.contains(s)) {
                    throw new BadRequestException(INVALID_SORT_FIELD,
                            INVALID_SORT_FIELD.getExternalMessage());
                }
            });
        }
    }

    protected void buildSearchResponse(SearchResponse searchResponse,
            ElasticResponse elasticResponse,
            ElasticRequest elasticRequest, String component, String filterNamespace,
            String searchResultNamespace, Classification classificationData, Client client) {
        if (elasticRequest.isSearchRequest()) {
            Map<String, Map<String, Object>> propertyMap = null;
            if (StringUtils.isNotBlank(component)) {
                propertyMap = propertyReader.getPropertiesAsMap(component, searchResultNamespace);
            }
            populateSearchResults(searchResponse, elasticResponse, propertyMap, elasticRequest, client);
            long total = elasticResponse.getTotalSearchResultsCount();
            searchResponse.setTotal(total);
        }
        if (elasticRequest.isAggregationRequest()) {
            Map<String, Map<String, Object>> propertyMap = null;
            if (StringUtils.isNotBlank(component)) {
                propertyMap = propertyReader
                        .getPropertiesAsMap(component, filterNamespace);
            }
            searchResponseBuilder
                    .populateSearchFilters(searchResponse, elasticResponse, elasticRequest,
                            propertyMap);
        }
        ClassificationResponse classificationResponse = new ClassificationResponse();
        if (Objects.isNull(classificationData)) {
            classificationResponse.setClassified(false);
        } else {
            classificationResponse.setClassified(true);
            classificationResponse.setFilters(classificationData.getFilters());
            classificationResponse.setTerm(classificationData.getTerm());
            classificationResponse.setSortParams(classificationData.getSortParams());
        }
        searchResponse.setClassificationResponseData(classificationResponse);
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
            ElasticRequest elasticRequest, Client client);
}
