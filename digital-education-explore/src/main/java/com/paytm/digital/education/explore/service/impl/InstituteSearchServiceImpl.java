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
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_FILTER_NAMESPACE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_GENDER;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_SEARCH_NAMESPACE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.MAX_RANK;
import static com.paytm.digital.education.explore.constants.ExploreConstants.OFFICIAL_NAME;
import static com.paytm.digital.education.explore.constants.ExploreConstants.OWNERSHIP;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SEARCH_ANALYZER_INSTITUTE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SEARCH_INDEX_INSTITUTE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.STATE_INSTITUTE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.STREAM_INSTITUTE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.UNIVERSITY_NAME_SEARCH;
import static com.paytm.digital.education.explore.constants.ExploreConstants.FORMER_NAME;
import static com.paytm.digital.education.explore.constants.ExploreConstants.FORMER_NAME_BOOST;
import static com.paytm.digital.education.explore.constants.ExploreConstants.OFFICIAL_NAME_SEARCH;
import static com.paytm.digital.education.explore.constants.ExploreConstants.ALTERNATE_NAMES_BOOST;
import static com.paytm.digital.education.explore.constants.ExploreConstants.ALTERNATE_NAMES;
import static com.paytm.digital.education.explore.constants.ExploreConstants.C0MMON_NAME_BOOST;
import static com.paytm.digital.education.explore.constants.ExploreConstants.COMMON_NAME;
import static com.paytm.digital.education.explore.constants.ExploreConstants.UNIVERSITY_NAME_SEARCH_BOOST;
import static com.paytm.digital.education.explore.constants.ExploreConstants.OTHER_NAMES_NGRAM;
import static com.paytm.digital.education.explore.constants.ExploreConstants.OTHER_NAMES_NGRAM_BOOST;
import static com.paytm.digital.education.explore.constants.ExploreConstants.OFFICIAL_NAME_SEARCH_BOOST;

import com.paytm.digital.education.elasticsearch.enums.DataSortOrder;
import com.paytm.digital.education.elasticsearch.enums.FilterQueryType;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.es.model.InstituteSearch;
import com.paytm.digital.education.explore.request.dto.search.SearchRequest;
import com.paytm.digital.education.explore.response.dto.common.OfficialAddress;
import com.paytm.digital.education.explore.response.dto.search.InstituteData;
import com.paytm.digital.education.explore.response.dto.search.SearchBaseData;
import com.paytm.digital.education.explore.response.dto.search.SearchResponse;
import com.paytm.digital.education.explore.response.dto.search.SearchResult;
import com.paytm.digital.education.explore.service.helper.SearchAggregateHelper;
import com.paytm.digital.education.explore.utility.CommonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import javax.annotation.PostConstruct;


@Slf4j
@Service
@AllArgsConstructor
public class InstituteSearchServiceImpl extends AbstractSearchServiceImpl {

    private static Map<String, FilterQueryType> filterQueryTypeMap;
    private static Map<String, Float>           searchFieldKeys;
    private static Map<String, DataSortOrder>   sortKeysInOrder;
    private        SearchAggregateHelper        searchAggregateHelper;

    @PostConstruct
    private void init() {
        filterQueryTypeMap = new HashMap<>();
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
        searchFieldKeys = new HashMap<>();
        searchFieldKeys.put(OFFICIAL_NAME_SEARCH, OFFICIAL_NAME_SEARCH_BOOST);
        searchFieldKeys.put(FORMER_NAME, FORMER_NAME_BOOST);
        searchFieldKeys.put(COMMON_NAME, C0MMON_NAME_BOOST);
        searchFieldKeys.put(ALTERNATE_NAMES, ALTERNATE_NAMES_BOOST);
        searchFieldKeys.put(UNIVERSITY_NAME_SEARCH, UNIVERSITY_NAME_SEARCH_BOOST);
        searchFieldKeys.put(OTHER_NAMES_NGRAM, OTHER_NAMES_NGRAM_BOOST);
        sortKeysInOrder = new LinkedHashMap<String, DataSortOrder>();
        sortKeysInOrder.put(MAX_RANK, DataSortOrder.ASC);
        sortKeysInOrder.put(OFFICIAL_NAME, DataSortOrder.ASC);
    }

    @Override
    @Cacheable(value = "institute_search")
    public SearchResponse search(SearchRequest searchRequest) throws IOException, TimeoutException {
        validateRequest(searchRequest, filterQueryTypeMap);
        ElasticRequest elasticRequest = buildSearchRequest(searchRequest);
        ElasticResponse elasticResponse = initiateSearch(elasticRequest, InstituteSearch.class);
        return buildSearchResponse(elasticResponse, elasticRequest, EXPLORE_COMPONENT,
                INSTITUTE_FILTER_NAMESPACE, INSTITUTE_SEARCH_NAMESPACE);
    }

    @Override
    protected ElasticRequest buildSearchRequest(SearchRequest searchRequest) {
        ElasticRequest elasticRequest =
                createSearchRequest(searchRequest, SEARCH_ANALYZER_INSTITUTE,
                        SEARCH_INDEX_INSTITUTE);
        populateSearchFields(searchRequest, elasticRequest, searchFieldKeys, InstituteSearch.class);
        populateFilterFields(searchRequest, elasticRequest, InstituteSearch.class,
                filterQueryTypeMap);
        populateAggregateFields(searchRequest, elasticRequest,
                searchAggregateHelper.getInstituteAggregateData(), InstituteSearch.class);
        if (StringUtils.isBlank(searchRequest.getTerm())) {
            populateSortFields(searchRequest, elasticRequest, InstituteSearch.class,
                    sortKeysInOrder);
        }
        return elasticRequest;
    }

    @Override
    protected void populateSearchResults(SearchResponse searchResponse,
            ElasticResponse elasticResponse, Map<String, Map<String, Object>> properties) {
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
                instituteData.setApprovals(CommonUtil.getApprovals(instituteSearch.getApprovedBy(),
                        instituteSearch.getUniversityName()));
                instituteData.setExams(instituteSearch.getExamsAccepted());
                if (StringUtils.isNotBlank(instituteSearch.getImageLink())) {
                    instituteData
                            .setLogoUrl(CommonUtil.getLogoLink(instituteSearch.getImageLink()));
                }

                OfficialAddress officialAddress =
                        CommonUtil.getOfficialAddress(instituteSearch.getState(),
                                instituteSearch.getCity(), null, null, null);
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
