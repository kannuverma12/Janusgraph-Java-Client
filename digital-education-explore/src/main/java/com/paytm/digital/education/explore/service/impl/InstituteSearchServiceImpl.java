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
import static com.paytm.digital.education.explore.constants.ExploreConstants.UNIVERSITY_NAME;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_SEARCH_NAMESPACE;
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
import static com.paytm.digital.education.explore.constants.ExploreConstants.NGRAM;
import static com.paytm.digital.education.explore.constants.ExploreConstants.OTHER_NAMES_NGRAM_BOOST;
import static com.paytm.digital.education.explore.constants.ExploreConstants.OFFICIAL_NAME_SEARCH_BOOST;
import static com.paytm.digital.education.explore.constants.ExploreConstants.CITY_SEARCH;
import static com.paytm.digital.education.explore.constants.ExploreConstants.STATE_SEARCH;
import static com.paytm.digital.education.explore.constants.ExploreConstants.FE_RANK_SORT;
import static com.paytm.digital.education.explore.constants.ExploreConstants.DB_RANK_OVERALL;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_NAMESPACE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SORT_PARAM_KEY;
import static com.paytm.digital.education.explore.constants.ExploreConstants.STOPWORDS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.STOPWORDS_KEY;
import static com.paytm.digital.education.explore.constants.ExploreConstants.TIE_BREAKER;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paytm.digital.education.elasticsearch.enums.DataSortOrder;
import com.paytm.digital.education.elasticsearch.enums.FilterQueryType;
import com.paytm.digital.education.elasticsearch.models.CrossField;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.explore.database.entity.SearchSortParam;
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
import com.paytm.digital.education.property.reader.PropertyReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
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
    private static Map<String, Float>           locationSearchFieldKeys;
    private        SearchAggregateHelper        searchAggregateHelper;
    private        ClassifierSearchService      classifierSearchService;
    private        PropertyReader               propertyReader;

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
        filterQueryTypeMap.put(UNIVERSITY_NAME, TERMS);
        filterQueryTypeMap.put(ESTABLISHMENT_YEAR, RANGE);
        locationSearchFieldKeys = new HashMap<>();
        locationSearchFieldKeys.put(STATE_SEARCH, 0.001F);
        locationSearchFieldKeys.put(CITY_SEARCH, 0.001F);
        searchFieldKeys = new HashMap<>();
        searchFieldKeys.put(OFFICIAL_NAME_SEARCH, OFFICIAL_NAME_SEARCH_BOOST);
        searchFieldKeys.put(FORMER_NAME, FORMER_NAME_BOOST);
        searchFieldKeys.put(COMMON_NAME, C0MMON_NAME_BOOST);
        searchFieldKeys.put(ALTERNATE_NAMES, ALTERNATE_NAMES_BOOST);
        searchFieldKeys.put(UNIVERSITY_NAME_SEARCH, UNIVERSITY_NAME_SEARCH_BOOST);
        searchFieldKeys.put(FORMER_NAME + NGRAM, OTHER_NAMES_NGRAM_BOOST);
        searchFieldKeys.put(COMMON_NAME + NGRAM, OTHER_NAMES_NGRAM_BOOST);
        searchFieldKeys.put(ALTERNATE_NAMES + NGRAM, OTHER_NAMES_NGRAM_BOOST);
        searchFieldKeys.put(UNIVERSITY_NAME_SEARCH + NGRAM, OTHER_NAMES_NGRAM_BOOST);
        searchFieldKeys.put(OFFICIAL_NAME + NGRAM, OTHER_NAMES_NGRAM_BOOST);
    }

    @Override
    @Cacheable(value = "institute_search")
    public SearchResponse search(SearchRequest searchRequest) throws IOException, TimeoutException {
        validateRequest(searchRequest, filterQueryTypeMap);
        if (StringUtils.isNotBlank(searchRequest.getTerm())) {
            Map<String, Object> stopWords = propertyReader
                    .getPropertiesAsMapByKey(EXPLORE_COMPONENT, INSTITUTE_NAMESPACE, STOPWORDS_KEY);
            String refinedTerm =
                    CommonUtil.removeWordsFromString(searchRequest.getTerm().toLowerCase(),
                            (List<String>) stopWords.get(STOPWORDS), " ");
            searchRequest.setTerm(refinedTerm);
            if (StringUtils.isNotBlank(refinedTerm)) {
                classifierSearchService.classify(searchRequest);
            }
        }
        ElasticRequest elasticRequest = buildSearchRequest(searchRequest);
        ElasticResponse elasticResponse = initiateSearch(elasticRequest, InstituteSearch.class);
        return buildSearchResponse(elasticResponse, elasticRequest, EXPLORE_COMPONENT,
                INSTITUTE_FILTER_NAMESPACE, INSTITUTE_SEARCH_NAMESPACE,
                searchRequest.getClassificationData());
    }

    private List<SearchSortParam> convertMapToPojo(List<Object> data) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(data, new TypeReference<List<SearchSortParam>>() {
        });
    }

    private List<SearchSortParam> getSortParamsFromDb(SearchRequest searchRequest) {
        Map<String, Object> sortParamsFromDb = propertyReader
                .getPropertiesAsMapByKey(EXPLORE_COMPONENT, INSTITUTE_NAMESPACE,
                        SORT_PARAM_KEY);
        if (!CollectionUtils.isEmpty(sortParamsFromDb)) {
            List<SearchSortParam> sortParams = new ArrayList<>();
            if (!CollectionUtils.isEmpty(searchRequest.getFilter()) && searchRequest.getFilter()
                    .containsKey(STREAM_INSTITUTE)) {
                /*
                 * Applying sorting based on only one stream in case multiple stream filters are applied
                 * */
                String filterKey = (String) searchRequest.getFilter().get(STREAM_INSTITUTE).get(0);
                if (sortParamsFromDb.containsKey(filterKey)) {
                    sortParams.addAll(convertMapToPojo(
                            (List<Object>) sortParamsFromDb.get(filterKey)));
                }
            }
            sortParams
                    .addAll(convertMapToPojo((List<Object>) sortParamsFromDb.get(DB_RANK_OVERALL)));
            return sortParams;
        }
        return null;
    }

    /*
     * 1. Get sort params from classification
     * 2. Get from DB
     * 3. replace with null
     * */
    private void replaceWithRankSortParams(SearchRequest searchRequest) {
        if (Objects.nonNull(searchRequest.getClassificationData()) && !CollectionUtils
                .isEmpty(searchRequest.getClassificationData().getSortParams())) {
            searchRequest.setSortOrder(searchRequest.getClassificationData().getSortParams());
        } else {
            List<SearchSortParam> dbSortParams = getSortParamsFromDb(searchRequest);
            if (CollectionUtils.isEmpty(dbSortParams)) {
                searchRequest.setSortOrder(null);
                return;
            }
            LinkedHashMap<String, DataSortOrder> sortParamsOfRequest = new LinkedHashMap<>();
            for (int i = 0; i < dbSortParams.size(); i++) {
                sortParamsOfRequest
                        .put(dbSortParams.get(i).getField(), dbSortParams.get(i).getOrder());
            }
            searchRequest.setSortOrder(sortParamsOfRequest);
        }
    }

    private void populateSearchQueryType(ElasticRequest elasticRequest, Float tieBreaker) {
        CrossField searchQueryType = new CrossField();
        searchQueryType.setTieBreaker(tieBreaker);
        elasticRequest.setSearchQueryType(searchQueryType);
    }

    @Override
    protected ElasticRequest buildSearchRequest(SearchRequest searchRequest) {
        ElasticRequest elasticRequest =
                createSearchRequest(searchRequest, SEARCH_ANALYZER_INSTITUTE,
                        SEARCH_INDEX_INSTITUTE);
        Map<String, Float> searchKeys = searchFieldKeys;
        if (Objects.nonNull(searchRequest.getClassificationData()) && searchRequest
                .getClassificationData().isLocationClassified()) {
            searchKeys.putAll(locationSearchFieldKeys);
        }
        populateSearchFields(searchRequest, elasticRequest, searchKeys,
                InstituteSearch.class);
        populateFilterFields(searchRequest, elasticRequest, InstituteSearch.class,
                filterQueryTypeMap);
        populateAggregateFields(searchRequest, elasticRequest,
                searchAggregateHelper.getInstituteAggregateData(), InstituteSearch.class);
        populateSearchQueryType(elasticRequest, TIE_BREAKER);
        /*
         * Sort on rank will be done
         * 1. when search term is empty
         * 2. a query is classified :
         * 3. sort order is ranking
         * */
        if (StringUtils.isBlank(searchRequest.getTerm()) || (
                Objects.nonNull(searchRequest.getClassificationData()) && !CollectionUtils
                        .isEmpty(searchRequest.getClassificationData().getSortParams())) || (
                !CollectionUtils.isEmpty(searchRequest.getSortOrder()) && searchRequest
                        .getSortOrder().containsKey(FE_RANK_SORT))) {
            replaceWithRankSortParams(searchRequest);
        } else {
            searchRequest.setSortOrder(null);
        }
        populateSortFields(searchRequest, elasticRequest, InstituteSearch.class);
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
