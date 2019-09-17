package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.elasticsearch.enums.FilterQueryType;
import com.paytm.digital.education.elasticsearch.models.CrossField;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.explore.database.entity.SchoolPaytmKeys;
import com.paytm.digital.education.explore.enums.Client;
import com.paytm.digital.education.elasticsearch.models.SortField;
import com.paytm.digital.education.explore.constants.ExploreConstants;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.es.model.GeoLocation;
import com.paytm.digital.education.explore.es.model.SchoolSearch;
import com.paytm.digital.education.explore.request.dto.search.SearchRequest;
import com.paytm.digital.education.explore.response.dto.common.OfficialAddress;
import com.paytm.digital.education.explore.response.dto.search.SchoolSearchData;
import com.paytm.digital.education.explore.response.dto.search.SearchBaseData;
import com.paytm.digital.education.explore.response.dto.search.SearchResponse;
import com.paytm.digital.education.explore.response.dto.search.SearchResult;
import com.paytm.digital.education.explore.service.helper.SearchAggregateHelper;
import com.paytm.digital.education.explore.utility.CommonUtil;
import com.paytm.digital.education.property.reader.PropertyReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import static com.paytm.digital.education.elasticsearch.enums.FilterQueryType.RANGE;
import static com.paytm.digital.education.elasticsearch.enums.FilterQueryType.TERMS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SORT_DISTANCE_FIELD;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EMPTY_STRING;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXPLORE_COMPONENT;
import static com.paytm.digital.education.explore.constants.ExploreConstants.NGRAM;
import static com.paytm.digital.education.explore.constants.ExploreConstants.OTHER_NAMES_NGRAM_BOOST;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SCHOOL_FILTER_NAMESPACE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SCHOOL_SEARCH_NAMESPACE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SEARCH_ANALYZER_SCHOOL;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SEARCH_INDEX_SCHOOL;
import static com.paytm.digital.education.explore.constants.ExploreConstants.STOPWORDS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.STOPWORDS_KEY;
import static com.paytm.digital.education.explore.constants.ExploreConstants.TIE_BREAKER;
import static com.paytm.digital.education.explore.constants.SchoolConstants.NAMES;
import static com.paytm.digital.education.explore.constants.SchoolConstants.NAMES_SEARCH_BOOST;
import static com.paytm.digital.education.explore.constants.SchoolConstants.OFFICIAL_NAME_SEARCH;
import static com.paytm.digital.education.explore.constants.SchoolConstants.OFFICIAL_NAME;
import static com.paytm.digital.education.explore.constants.SchoolConstants.SCHOOL_BOARDS_ACCEPTED;
import static com.paytm.digital.education.explore.constants.SchoolConstants.SCHOOL_BOARDS_EDUCATION_LEVEL;
import static com.paytm.digital.education.explore.constants.SchoolConstants.SCHOOL_BOARDS_ESTABLISHMENT_YEAR;
import static com.paytm.digital.education.explore.constants.SchoolConstants.SCHOOL_BOARDS_FEE;
import static com.paytm.digital.education.explore.constants.SchoolConstants.SCHOOL_BOARDS_GENDER_ACCEPTED;
import static com.paytm.digital.education.explore.constants.SchoolConstants.SCHOOL_BOARDS_OWNERSHIP;
import static com.paytm.digital.education.explore.constants.SchoolConstants.SCHOOL_CITY;
import static com.paytm.digital.education.explore.constants.SchoolConstants.SCHOOL_FACILITIES;
import static com.paytm.digital.education.explore.constants.SchoolConstants.SCHOOL_ID;
import static com.paytm.digital.education.explore.constants.SchoolConstants.SCHOOL_MEDIUM;
import static com.paytm.digital.education.explore.constants.SchoolConstants.SCHOOL_STATE;

@Slf4j
@Service
@AllArgsConstructor
public class SchoolSearchServiceImpl extends AbstractSearchServiceImpl {

    private static Map<String, FilterQueryType> filterQueryTypeMap;
    private static Map<String, Float>           searchFieldKeys;
    private static Map<String, Float>           locationSearchFieldKeys;
    private        SearchAggregateHelper        searchAggregateHelper;
    private        PropertyReader               propertyReader;
    private static DecimalFormat                df = new DecimalFormat("#.#");

    @PostConstruct
    private void init() {
        filterQueryTypeMap = new HashMap<>();
        filterQueryTypeMap.put(SCHOOL_CITY, TERMS);
        filterQueryTypeMap.put(SCHOOL_STATE, TERMS);
        filterQueryTypeMap.put(SCHOOL_BOARDS_EDUCATION_LEVEL, TERMS);
        filterQueryTypeMap.put(SCHOOL_BOARDS_FEE, RANGE);
        filterQueryTypeMap.put(SCHOOL_ID, TERMS);
        filterQueryTypeMap.put(SCHOOL_BOARDS_OWNERSHIP, TERMS);
        filterQueryTypeMap.put(SCHOOL_FACILITIES, TERMS);
        filterQueryTypeMap.put(SCHOOL_BOARDS_GENDER_ACCEPTED, TERMS);
        filterQueryTypeMap.put(SCHOOL_BOARDS_ACCEPTED, TERMS);
        filterQueryTypeMap.put(SCHOOL_MEDIUM, TERMS);
        filterQueryTypeMap.put(SCHOOL_BOARDS_ESTABLISHMENT_YEAR, RANGE);
        locationSearchFieldKeys = new HashMap<>();
        locationSearchFieldKeys.put(SCHOOL_STATE, 0.001F);
        locationSearchFieldKeys.put(SCHOOL_CITY, 0.001F);
        searchFieldKeys = new HashMap<>();
        searchFieldKeys.put(OFFICIAL_NAME_SEARCH, NAMES_SEARCH_BOOST);
        searchFieldKeys.put(NAMES, NAMES_SEARCH_BOOST);
        searchFieldKeys.put(OFFICIAL_NAME + NGRAM, OTHER_NAMES_NGRAM_BOOST);
        searchFieldKeys.put(NAMES + NGRAM, OTHER_NAMES_NGRAM_BOOST);
    }

    @Override
    @Cacheable(value = "school_search")
    public SearchResponse search(SearchRequest schoolSearchRequest)
            throws IOException, TimeoutException {
        validateRequest(schoolSearchRequest, filterQueryTypeMap);
        SearchResponse searchResponse = new SearchResponse(schoolSearchRequest.getTerm());
        if (!schoolSearchRequest.isClearFilters() && StringUtils
                .isNotBlank(schoolSearchRequest.getTerm())) {
            Map<String, Object> stopWords = propertyReader
                    .getPropertiesAsMapByKey(EXPLORE_COMPONENT, SCHOOL_SEARCH_NAMESPACE,
                            STOPWORDS_KEY);
            String refinedTerm =
                    CommonUtil.removeWordsFromString(schoolSearchRequest.getTerm().toLowerCase(),
                            (List<String>) stopWords.get(STOPWORDS), EMPTY_STRING);
            schoolSearchRequest.setTerm(refinedTerm);
        }
        ElasticRequest elasticRequest = buildSearchRequest(schoolSearchRequest);
        ElasticResponse elasticResponse = initiateSearch(elasticRequest, SchoolSearch.class);
        buildSearchResponse(searchResponse, elasticResponse, elasticRequest, EXPLORE_COMPONENT,
                SCHOOL_FILTER_NAMESPACE, SCHOOL_SEARCH_NAMESPACE,
                schoolSearchRequest.getClassificationData(), schoolSearchRequest.getClient());
        return searchResponse;
    }

    @Override
    protected ElasticRequest buildSearchRequest(SearchRequest schoolSearchRequest) {
        ElasticRequest elasticRequest =
                createSearchRequest(schoolSearchRequest, SEARCH_ANALYZER_SCHOOL,
                        SEARCH_INDEX_SCHOOL);
        Map<String, Float> searchKeys = searchFieldKeys;
        if (Objects.nonNull(schoolSearchRequest.getClassificationData()) && schoolSearchRequest
                .getClassificationData().isLocationClassified()) {
            searchKeys.putAll(locationSearchFieldKeys);
        }
        populateSearchFields(schoolSearchRequest, elasticRequest, searchKeys,
                SchoolSearch.class);
        populateFilterFields(schoolSearchRequest, elasticRequest, SchoolSearch.class,
                filterQueryTypeMap);
        populateAggregateFields(schoolSearchRequest, elasticRequest,
                searchAggregateHelper.getSchoolAggregateData(), SchoolSearch.class);
        populateSearchQueryType(elasticRequest, TIE_BREAKER);

        /**
         * Sort on geolocation when
         * 1.Source geolocation is present
         * 2.SortOrder contains "location" field
         */
        if (schoolSearchRequest.getGeoLocation() != null
                && schoolSearchRequest.getSortOrder() != null
                && schoolSearchRequest.getSortOrder().containsKey(SORT_DISTANCE_FIELD)) {
            populateNearbyFilterFields(schoolSearchRequest, elasticRequest);
        } else {
            schoolSearchRequest.setSortOrder(null);
        }

        populateSortFields(schoolSearchRequest, elasticRequest, SchoolSearch.class);
        return elasticRequest;
    }

    private void populateNearbyFilterFields(SearchRequest schoolSearchRequest,
            ElasticRequest elasticRequest) {
        GeoLocation geoLocationData = schoolSearchRequest.getGeoLocation();
        elasticRequest.setLocationLatLon(Arrays.asList(geoLocationData.getLat(),
                geoLocationData.getLon()));
    }

    private void populateSearchQueryType(ElasticRequest elasticRequest, Float tieBreaker) {
        CrossField searchQueryType = new CrossField();
        searchQueryType.setTieBreaker(tieBreaker);
        elasticRequest.setSearchQueryType(searchQueryType);
    }

    @Override
    protected void populateSearchResults(SearchResponse searchResponse,
            ElasticResponse elasticResponse, Map<String, Map<String, Object>> properties,
            ElasticRequest elasticRequest, Client client) {
        List<SchoolSearch> schoolSearches = elasticResponse.getDocuments();
        SearchResult searchResults = new SearchResult();
        Map<Long, SearchBaseData> schoolDataMap = new HashMap<Long, SearchBaseData>();
        boolean isGeoDistanceSortRequest = isGeoDistanceSortRequest(elasticRequest);
        Integer indexOfGeoDistanceSortInElasticRequest = null;

        if (ArrayUtils.isNotEmpty(elasticRequest.getSortFields()) && isGeoDistanceSortRequest) {
            indexOfGeoDistanceSortInElasticRequest = getIndexOfGeoDistanceSortInElasticRequest(
                    elasticRequest.getSortFields());
        }

        if (!CollectionUtils.isEmpty(schoolSearches)) {
            searchResults.setEntity(EducationEntity.SCHOOL);
            List<SearchBaseData> schoolDataList = new ArrayList<SearchBaseData>();
            Integer finalIndexOfGeoDistance = indexOfGeoDistanceSortInElasticRequest;
            schoolSearches.forEach(schoolSearch -> {
                SchoolSearchData schoolSearchData = new SchoolSearchData();
                schoolSearchData.setSchoolId(schoolSearch.getSchoolId());
                schoolSearchData.setOfficialName(schoolSearch.getOfficialName());
                schoolSearchData.setUrlDisplayName(
                        CommonUtil.convertNameToUrlDisplayName(schoolSearch.getOfficialName()));
                if (StringUtils.isNotBlank(schoolSearch.getImageLink())) {
                    schoolSearchData
                            .setLogoUrl(CommonUtil.getLogoLink(schoolSearch.getImageLink(),
                                    EducationEntity.SCHOOL));
                }
                OfficialAddress officialAddress =
                        CommonUtil.getOfficialAddress(schoolSearch.getState(),
                                schoolSearch.getCity(), null, null, null);
                schoolSearchData.setOfficialAddress(officialAddress);

                setSchoolGeoDistanceString(isGeoDistanceSortRequest, finalIndexOfGeoDistance,
                        schoolSearch,
                        schoolSearchData);

                //"isClient" info will be used in future in get updates feature
                schoolSearchData.setClient(false);
                schoolSearchData.setBrochureUrl(schoolSearch.getBrochureUrl());
                if (Objects.nonNull(schoolSearch.getPaytmKeys())) {
                    Optional<SchoolPaytmKeys> schoolPaytmKeys = Optional.ofNullable(schoolSearch.getPaytmKeys());
                    schoolSearchData.setPid(schoolPaytmKeys.map(SchoolPaytmKeys::getPid).orElse(null));
                    schoolSearchData.setFormId(schoolPaytmKeys.map(SchoolPaytmKeys::getFormId).orElse(null));
                }
                schoolSearchData.setCtaList(ctaHelper.buildCTA(schoolSearchData, client));
                schoolDataMap.put(schoolSearch.getSchoolId(), schoolSearchData);
                schoolDataList.add(schoolSearchData);
            });
            searchResults.setValues(schoolDataList);
        }
        searchResponse.setEntityDataMap(schoolDataMap);
        searchResponse.setResults(searchResults);
    }

    private void setSchoolGeoDistanceString(boolean isGeoDistanceSortRequest,
            Integer finalIndexOfGeoDistance, SchoolSearch schoolSearch,
            SchoolSearchData schoolSearchData) {
        if (!CollectionUtils.isEmpty(schoolSearch.getSort())
                && isGeoDistanceSortRequest && finalIndexOfGeoDistance != null) {
            Double distanceInKilometers =
                    schoolSearch.getSort().get(finalIndexOfGeoDistance);
            if (Double.isFinite(distanceInKilometers)) {
                schoolSearchData.setDistance(df.format(distanceInKilometers)
                        + ExploreConstants.DISTANCE_KILOMETERS);
            }
        }
    }

    private boolean isGeoDistanceSortRequest(ElasticRequest elasticRequest) {
        if (CollectionUtils.isEmpty(elasticRequest.getLocationLatLon())
                || elasticRequest.getLocationLatLon().size() != 2
                || ArrayUtils.isEmpty(elasticRequest.getSortFields())) {
            return false;
        }
        SortField[] sortFields = elasticRequest.getSortFields();

        for (SortField sortField : sortFields) {
            if (SORT_DISTANCE_FIELD.equalsIgnoreCase(sortField.getName())) {
                return true;
            }
        }
        return false;
    }

    private Integer getIndexOfGeoDistanceSortInElasticRequest(SortField[] sortFields) {
        Integer indexOfGeoDistanceSort = null;
        int currentSortFieldIndex = 0;
        if (ArrayUtils.isNotEmpty(sortFields)) {
            for (SortField sortField : sortFields) {
                if (SORT_DISTANCE_FIELD.equalsIgnoreCase(sortField.getName())) {
                    indexOfGeoDistanceSort = currentSortFieldIndex;
                }
                currentSortFieldIndex++;
            }
        }
        return indexOfGeoDistanceSort;
    }
}
