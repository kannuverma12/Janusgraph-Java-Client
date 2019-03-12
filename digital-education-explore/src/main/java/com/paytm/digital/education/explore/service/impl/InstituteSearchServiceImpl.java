package com.paytm.digital.education.explore.service.impl;

import static com.paytm.digital.education.elasticsearch.enums.FilterQueryType.RANGE;
import static com.paytm.digital.education.elasticsearch.enums.FilterQueryType.TERMS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.CITY_INSTITUTE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.COURSE_LEVEL_INSTITUTE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.ESTABLISHMENT_YEAR;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAMS_ACCEPTED_INSTITUTE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.FACILITIES;
import static com.paytm.digital.education.explore.constants.ExploreConstants.FEES_INSTITUTE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_GENDER;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.MAX_RANK_INSTITUTE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.OWNERSHIP;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SEARCH_NAMES_INSTITUTE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.STATE_INSTITUTE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.STREAM_INSTITUTE;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_RANGE_FILTER_VALUES;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.paytm.digital.education.elasticsearch.enums.AggregationType;
import com.paytm.digital.education.elasticsearch.enums.DataSortOrder;
import com.paytm.digital.education.elasticsearch.enums.FilterQueryType;
import com.paytm.digital.education.elasticsearch.models.AggregateField;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.elasticsearch.models.FilterField;
import com.paytm.digital.education.elasticsearch.models.SearchField;
import com.paytm.digital.education.elasticsearch.models.SortField;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.es.model.InstituteSearch;
import com.paytm.digital.education.explore.request.dto.search.SearchRequest;
import com.paytm.digital.education.explore.response.builders.SearchResponseBuilder;
import com.paytm.digital.education.explore.response.dto.common.OfficialAddress;
import com.paytm.digital.education.explore.response.dto.search.InstituteData;
import com.paytm.digital.education.explore.response.dto.search.SearchBaseData;
import com.paytm.digital.education.explore.response.dto.search.SearchResponse;
import com.paytm.digital.education.explore.response.dto.search.SearchResult;
import com.paytm.digital.education.explore.service.helper.SearchAggregateHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class InstituteSearchServiceImpl extends AbstractSearchServiceImpl {

    private SearchResponseBuilder               searchResponseBuilder;
    private static Map<String, FilterQueryType> filterQueryTypeMap;
    private static List<String>                 searchFieldKeys;
    private static List<String>                 sortKeysInOrder;

    @PostConstruct
    private void init() {
        filterQueryTypeMap = new HashMap<String, FilterQueryType>();
        filterQueryTypeMap.put(STATE_INSTITUTE, TERMS);
        filterQueryTypeMap.put(CITY_INSTITUTE, TERMS);
        filterQueryTypeMap.put(STREAM_INSTITUTE, TERMS);
        filterQueryTypeMap.put(COURSE_LEVEL_INSTITUTE, TERMS);
        filterQueryTypeMap.put(EXAMS_ACCEPTED_INSTITUTE, TERMS);
        filterQueryTypeMap.put(FEES_INSTITUTE, RANGE);
        filterQueryTypeMap.put(INSTITUTE_ID, TERMS);
        filterQueryTypeMap.put(OWNERSHIP, TERMS);
        filterQueryTypeMap.put(FACILITIES, TERMS);
        filterQueryTypeMap.put(INSTITUTE_GENDER, TERMS);
        filterQueryTypeMap.put(ESTABLISHMENT_YEAR, RANGE);
        searchFieldKeys = Arrays.asList(SEARCH_NAMES_INSTITUTE);
        sortKeysInOrder = Arrays.asList(MAX_RANK_INSTITUTE);
    }

    @Override
    public SearchResponse search(SearchRequest searchRequest) throws IOException,
            TimeoutException {
        ElasticRequest elasticRequest = buildSearchRequest(searchRequest);
        ElasticResponse elasticResponse = initiateSearch(elasticRequest, InstituteSearch.class);
        return buildSearchResponse(elasticResponse, elasticRequest);
    }

    @Override
    protected ElasticRequest buildSearchRequest(SearchRequest searchRequest) {
        ElasticRequest elasticRequest = createSearchRequest(searchRequest);
        populateSearchFields(searchRequest, elasticRequest);
        populateFilterFields(searchRequest, elasticRequest);
        populateAggregateFields(searchRequest, elasticRequest);
        populateSortFields(searchRequest, elasticRequest);
        return elasticRequest;
    }

    @Override
    protected void populateSearchFields(SearchRequest searchRequest,
            ElasticRequest elasticRequest) {
        if (StringUtils.isNotBlank(searchRequest.getTerm())) {
            SearchField[] searchFields = new SearchField[searchFieldKeys.size()];
            int i = 0;
            for (String searchKey : searchFieldKeys) {
                SearchField searchField = new SearchField();
                searchField.setName(searchKey);
                searchField.setPath(hierarchyMap.get(InstituteSearch.class).get(searchKey));
                searchField.setBoost(1.0f);
                searchFields[i++] = searchField;
            }
            elasticRequest.setSearchFields(searchFields);
        }
    }

    @Override
    protected void populateFilterFields(SearchRequest searchRequest,
            ElasticRequest elasticRequest) {
        if (!CollectionUtils.isEmpty(searchRequest.getFilter())) {
            int filterSize = searchRequest.getFilter().size();
            FilterField[] filterFields = new FilterField[filterSize];
            int i = 0;
            for (String filterKey : searchRequest.getFilter().keySet()) {
                FilterField filterField = new FilterField();
                filterField.setName(filterKey);
                filterField.setPath(hierarchyMap.get(InstituteSearch.class).get(filterKey));
                filterField.setType(filterQueryTypeMap.get(filterKey));
                if (RANGE.equals(filterQueryTypeMap.get(filterKey))) {
                    List<Object> values = searchRequest.getFilter().get(filterKey);
                    if (CollectionUtils.isEmpty(values) || values.size() < 2) {
                        throw new BadRequestException(INVALID_RANGE_FILTER_VALUES,
                                INVALID_RANGE_FILTER_VALUES.getExternalMessage(),
                                new Object[] {filterKey});
                    }
                }
                filterField.setValues(searchRequest.getFilter().get(filterKey));
                filterFields[i++] = filterField;
            }
            elasticRequest.setFilterFields(filterFields);
        }

    }

    @Override
    protected void populateAggregateFields(SearchRequest searchRequest,
            ElasticRequest elasticRequest) {
        if (searchRequest.isFetchFilter()) {
            AggregateField[] aggregateFields = SearchAggregateHelper.getInstituteAggregateData();

            Map<String, List<Object>> filters = searchRequest.getFilter();

            for (int i = 0; i < aggregateFields.length; i++) {
                aggregateFields[i].setPath(
                        hierarchyMap.get(InstituteSearch.class).get(aggregateFields[i].getName()));

                if (aggregateFields[i].getType() == AggregationType.TERMS
                        && filters.containsKey(aggregateFields[i].getName())) {
                    if (!CollectionUtils.isEmpty(filters.get(aggregateFields[i].getName()))) {
                        // TODO: need a sol, as ES include exclude takes only long[] and String[]
                        Object[] values = filters.get(aggregateFields[i].getName()).toArray();
                        String[] valuesStr = Arrays.copyOf(values, values.length, String[].class);
                        aggregateFields[i].setValues(valuesStr);
                    }
                }
            }
            elasticRequest.setAggregateFields(aggregateFields);
        }
    }

    @Override
    protected void populateSortFields(SearchRequest searchRequest, ElasticRequest elasticRequest) {
        if (!CollectionUtils.isEmpty(sortKeysInOrder)) {
            SortField[] sortFields = new SortField[sortKeysInOrder.size()];
            int i = 0;
            for (String keyName : sortKeysInOrder) {
                SortField sortField = new SortField();
                sortField.setName(keyName);
                sortField.setPath(hierarchyMap.get(InstituteSearch.class).get(keyName));
                sortField.setOrder(DataSortOrder.ASC);
                sortFields[i++] = sortField;
            }
            elasticRequest.setSortFields(sortFields);
        }
    }

    @Override
    protected SearchResponse buildSearchResponse(ElasticResponse elasticResponse,
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

    private void populateSearchResults(SearchResponse searchResponse,
            ElasticResponse<InstituteSearch> elasticResponse) {
        List<InstituteSearch> instituteSearches = elasticResponse.getDocuments();
        SearchResult searchResults = new SearchResult();
        Map<Long, SearchBaseData> instituteDataMap = new HashMap<Long, SearchBaseData>();
        if (!CollectionUtils.isEmpty(instituteSearches)) {
            searchResults.setEntity(EducationEntity.INSTITUTE);
            List<SearchBaseData> instituteDataList = new ArrayList<SearchBaseData>();
            instituteSearches.forEach(instituteSearch -> {
                InstituteData instituteData = new InstituteData();
                instituteData.setInstituteId(instituteSearch.getInstituteId());
                instituteData.setOfficialName(instituteSearch.getOfficialName());
                instituteData.setApprovals(instituteSearch.getApprovedBy());
                instituteData.setExams(instituteSearch.getExamsAccepted());
                OfficialAddress officialAddress =
                        OfficialAddress.builder().city(instituteSearch.getCity())
                                .state(instituteSearch.getState()).build();
                instituteData.setOfficialAddress(officialAddress);
                instituteDataMap.put(instituteSearch.getInstituteId(), instituteData);
                instituteDataList.add(instituteData);
            });
            searchResults.setValues(instituteDataList);
        }
        searchResponse.setEntityDataMap(instituteDataMap);
        searchResponse.setResults(searchResults);
    }
}
