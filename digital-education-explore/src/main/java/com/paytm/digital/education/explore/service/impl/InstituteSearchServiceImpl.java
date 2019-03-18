package com.paytm.digital.education.explore.service.impl;

import static com.paytm.digital.education.elasticsearch.enums.FilterQueryType.RANGE;
import static com.paytm.digital.education.elasticsearch.enums.FilterQueryType.TERMS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.CITY_INSTITUTE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.COURSE_LEVEL_INSTITUTE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.ESTABLISHMENT_YEAR;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAMS_ACCEPTED_INSTITUTE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXPLORE_COMPONENT;
import static com.paytm.digital.education.explore.constants.ExploreConstants.FACILITIES;
import static com.paytm.digital.education.explore.constants.ExploreConstants.FEES_INSTITUTE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_GENDER;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.MAX_RANK;
import static com.paytm.digital.education.explore.constants.ExploreConstants.OFFICIAL_NAME;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_FILTER_NAMESPACE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.OWNERSHIP;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SEARCH_ANALYZER;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SEARCH_INDEX;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SEARCH_NAMES_INSTITUTE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.STATE_INSTITUTE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.STREAM_INSTITUTE;
import com.paytm.digital.education.elasticsearch.enums.AggregationType;
import com.paytm.digital.education.elasticsearch.enums.FilterQueryType;
import com.paytm.digital.education.elasticsearch.models.AggregateField;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
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
import com.paytm.digital.education.property.reader.PropertyReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import javax.annotation.PostConstruct;

@Slf4j
@Service
@AllArgsConstructor
public class InstituteSearchServiceImpl extends AbstractSearchServiceImpl {

    private static Map<String, FilterQueryType> filterQueryTypeMap;
    private static List<String>                 searchFieldKeys;
    private static List<String>                 sortKeysInOrder;
    private SearchAggregateHelper               searchAggregateHelper;

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
        sortKeysInOrder = Arrays.asList(MAX_RANK, OFFICIAL_NAME);
    }

    @Override
    public SearchResponse search(SearchRequest searchRequest) throws IOException, TimeoutException {
        validateRequest(searchRequest, filterQueryTypeMap);
        ElasticRequest elasticRequest = buildSearchRequest(searchRequest);
        ElasticResponse elasticResponse = initiateSearch(elasticRequest, InstituteSearch.class);
        return buildSearchResponse(elasticResponse, elasticRequest, EXPLORE_COMPONENT,
                INSTITUTE_FILTER_NAMESPACE);
    }

    @Override
    protected ElasticRequest buildSearchRequest(SearchRequest searchRequest) {
        ElasticRequest elasticRequest =
                createSearchRequest(searchRequest, SEARCH_ANALYZER, SEARCH_INDEX);
        populateSearchFields(searchRequest, elasticRequest, searchFieldKeys, InstituteSearch.class);
        populateFilterFields(searchRequest, elasticRequest, InstituteSearch.class,
                filterQueryTypeMap);
        populateAggregateFields(searchRequest, elasticRequest);
        populateSortFields(searchRequest, elasticRequest, InstituteSearch.class, sortKeysInOrder);
        return elasticRequest;
    }

    @Override
    protected void populateSearchResults(SearchResponse searchResponse,
            ElasticResponse elasticResponse) {
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
                if (StringUtils.isNotBlank(instituteSearch.getImageLink())) {
                    instituteData.setLogoUrl(logoUrlPrefix + instituteSearch.getImageLink());
                }
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

    @Override
    protected void populateAggregateFields(SearchRequest searchRequest,
            ElasticRequest elasticRequest) {
        if (searchRequest.getFetchFilter()) {
            AggregateField[] aggregateFields = searchAggregateHelper.getInstituteAggregateData();
            Map<String, List<Object>> filters = searchRequest.getFilter();
            for (int i = 0; i < aggregateFields.length; i++) {
                aggregateFields[i].setPath(
                        hierarchyMap.get(InstituteSearch.class).get(aggregateFields[i].getName()));
                if (!CollectionUtils.isEmpty(filters)
                        && aggregateFields[i].getType() == AggregationType.TERMS
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

}
