package com.paytm.digital.education.coaching.consumer.service.search;

import com.paytm.digital.education.coaching.consumer.model.request.SearchRequest;
import com.paytm.digital.education.coaching.consumer.model.response.search.CoachingCenterData;
import com.paytm.digital.education.coaching.consumer.model.response.search.SearchBaseData;
import com.paytm.digital.education.coaching.consumer.model.response.search.SearchResponse;
import com.paytm.digital.education.coaching.consumer.model.response.search.SearchResult;
import com.paytm.digital.education.coaching.consumer.service.helper.CoachingSearchAggregateHelper;
import com.paytm.digital.education.coaching.es.model.CoachingCenterSearch;
import com.paytm.digital.education.coaching.es.model.CoachingInstituteSearch;
import com.paytm.digital.education.coaching.es.model.GeoLocation;
import com.paytm.digital.education.coaching.utils.ImageUtils;
import com.paytm.digital.education.elasticsearch.enums.FilterQueryType;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.elasticsearch.models.SortField;
import com.paytm.digital.education.enums.EducationEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_CENTER_CITY;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_CENTER_LOCATION;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_CENTER_PLACEHOLDER;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_CENTER_STATE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.DISTANCE_KILOMETERS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.INSTITUTE_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.COACHING_CENTER_NAME;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.COACHING_CENTER_NAME_BRAND_BOOST;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.SEARCH_ANALYZER_COACHING_CENTER;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.SEARCH_INDEX_COACHING_CENTER;
import static com.paytm.digital.education.constant.CommonConstants.COACHING_CENTER;
import static com.paytm.digital.education.elasticsearch.enums.FilterQueryType.TERMS;

@Slf4j
@Service
@AllArgsConstructor
public class CoachingCenterSearchService extends AbstractSearchService {

    private static DecimalFormat                 df = new DecimalFormat("#.#");
    private static Map<String, Float>            searchFieldKeys;
    private static Map<String, FilterQueryType>  filterQueryTypeMap;
    private        CoachingSearchAggregateHelper coachingSearchAggregateHelper;

    @PostConstruct
    public void init() {
        filterQueryTypeMap = new HashMap<>();
        filterQueryTypeMap.put(INSTITUTE_ID, TERMS);
        filterQueryTypeMap.put(COACHING_CENTER_CITY, TERMS);
        filterQueryTypeMap.put(COACHING_CENTER_STATE, TERMS);
        searchFieldKeys = new HashMap<>();
        searchFieldKeys.put(COACHING_CENTER_NAME, COACHING_CENTER_NAME_BRAND_BOOST);
    }

    @Override
    @Cacheable(value = "coaching_center_search")
    public SearchResponse search(SearchRequest searchRequest) {
        validateRequest(searchRequest, filterQueryTypeMap);
        ElasticRequest elasticRequest = buildSearchRequest(searchRequest);
        ElasticResponse elasticResponse;
        try {
            elasticResponse = initiateSearch(elasticRequest, CoachingCenterSearch.class);
        } catch (Exception e) {
            log.error("Error encountered in search query for coaching center request : {} {}",
                    elasticRequest, e);
            elasticResponse = new ElasticResponse();
        }
        SearchResponse searchResponse = new SearchResponse(searchRequest.getTerm());
        buildSearchResponse(searchResponse, elasticResponse, elasticRequest);
        return searchResponse;
    }

    @Override
    protected ElasticRequest buildSearchRequest(SearchRequest searchRequest) {
        ElasticRequest elasticRequest = createSearchRequest(searchRequest,
                SEARCH_ANALYZER_COACHING_CENTER, SEARCH_INDEX_COACHING_CENTER);
        populateSearchFields(searchRequest, elasticRequest, searchFieldKeys,
                CoachingInstituteSearch.class);
        populateFilterFields(searchRequest, elasticRequest, CoachingCenterSearch.class,
                filterQueryTypeMap);
        if (searchRequest.getFetchFilter()) {
            populateAggregateFields(searchRequest, elasticRequest,
                    coachingSearchAggregateHelper.getCoachingCenterAggregateData(),
                    CoachingCenterSearch.class);
        }
        if (searchRequest.getGeoLocation() != null
                && searchRequest.getSortOrder() != null
                && searchRequest.getSortOrder().containsKey(COACHING_CENTER_LOCATION)) {
            populateNearbyFilterFields(searchRequest, elasticRequest);
        } else {
            searchRequest.setSortOrder(null);
        }

        populateSortFields(searchRequest, elasticRequest, CoachingCenterSearch.class);
        return elasticRequest;
    }

    @Override
    protected void populateSearchResults(SearchResponse searchResponse,
            ElasticResponse elasticResponse, Map<String, Map<String, Object>> properties,
            ElasticRequest elasticRequest) {
        List<CoachingCenterSearch> coachingCenterSearches = elasticResponse.getDocuments();
        SearchResult searchResults = new SearchResult();
        boolean isGeoDistanceSortRequest = isGeoDistanceSortRequest(elasticRequest);
        Integer indexOfGeoDistanceSortInElasticRequest = null;
        if (ArrayUtils.isNotEmpty(elasticRequest.getSortFields()) && isGeoDistanceSortRequest) {
            indexOfGeoDistanceSortInElasticRequest = getIndexOfGeoDistanceSortInElasticRequest(
                    elasticRequest.getSortFields());
        }
        if (!CollectionUtils.isEmpty(coachingCenterSearches)) {
            searchResults.setEntity(EducationEntity.COACHING_CENTER);
            List<SearchBaseData> coachingCenterDataList = new ArrayList<>();
            for (CoachingCenterSearch coachingCenterSearch : coachingCenterSearches) {
                buildCoachingCenterData(isGeoDistanceSortRequest,
                        indexOfGeoDistanceSortInElasticRequest,
                        coachingCenterDataList, coachingCenterSearch);
            }
            searchResults.setValues(coachingCenterDataList);
        }
        searchResponse.setResults(searchResults);
    }

    private void buildCoachingCenterData(boolean isGeoDistanceSortRequest,
            Integer indexOfGeoDistanceSortInElasticRequest,
            List<SearchBaseData> coachingCenterDataList,
            CoachingCenterSearch coachingCenterSearch) {
        CoachingCenterData toAdd = CoachingCenterData
                .builder()
                .centerId(coachingCenterSearch.getCenterId())
                .instituteId(coachingCenterSearch.getInstituteId())
                .officialName(coachingCenterSearch.getOfficialName())
                .centerImage(ImageUtils
                        .getImageWithAbsolutePath(coachingCenterSearch.getCenterImage(),
                                COACHING_CENTER_PLACEHOLDER, COACHING_CENTER))
                .openingTime(coachingCenterSearch.getOpeningTime())
                .closingTime(coachingCenterSearch.getClosingTime())
                .addressLine1(coachingCenterSearch.getAddressLine1())
                .addressLine2(coachingCenterSearch.getAddressLine2())
                .addressLine3(coachingCenterSearch.getAddressLine3())
                .city(coachingCenterSearch.getCity())
                .state(coachingCenterSearch.getState())
                .pincode(coachingCenterSearch.getPincode())
                .phone(coachingCenterSearch.getPhone())
                .email(coachingCenterSearch.getEmail())
                .distance(getCoachingCenterGeoDistanceString(isGeoDistanceSortRequest,
                        indexOfGeoDistanceSortInElasticRequest,
                        coachingCenterSearch))
                .location(coachingCenterSearch.getLocation())
                .build();
        coachingCenterDataList.add(toAdd);
    }

    private String getCoachingCenterGeoDistanceString(boolean isGeoDistanceSortRequest,
            Integer finalIndexOfGeoDistance, CoachingCenterSearch coachingCenterSearch) {
        if (!CollectionUtils.isEmpty(coachingCenterSearch.getSort())
                && isGeoDistanceSortRequest && finalIndexOfGeoDistance != null) {
            Double distanceInKilometers =
                    coachingCenterSearch.getSort().get(finalIndexOfGeoDistance);
            if (Double.isFinite(distanceInKilometers)) {
                return df.format(distanceInKilometers)
                        + DISTANCE_KILOMETERS;
            }
        }
        return null;
    }

    private boolean isGeoDistanceSortRequest(ElasticRequest elasticRequest) {
        if (CollectionUtils.isEmpty(elasticRequest.getLocationLatLon())
                || elasticRequest.getLocationLatLon().size() != 2
                || ArrayUtils.isEmpty(elasticRequest.getSortFields())) {
            return false;
        }
        SortField[] sortFields = elasticRequest.getSortFields();
        for (SortField sortField : sortFields) {
            if (COACHING_CENTER_LOCATION.equalsIgnoreCase(sortField.getName())) {
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
                if (COACHING_CENTER_LOCATION.equalsIgnoreCase(sortField.getName())) {
                    indexOfGeoDistanceSort = currentSortFieldIndex;
                }
                currentSortFieldIndex++;
            }
        }
        return indexOfGeoDistanceSort;
    }

    private void populateNearbyFilterFields(SearchRequest searchRequest,
            ElasticRequest elasticRequest) {
        GeoLocation geoLocationData = searchRequest.getGeoLocation();
        elasticRequest.setLocationLatLon(Arrays.asList(geoLocationData.getLat(),
                geoLocationData.getLon()));
    }
}
