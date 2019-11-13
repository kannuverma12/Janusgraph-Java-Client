package com.paytm.digital.education.coaching.consumer.service.search;

import com.paytm.digital.education.coaching.consumer.model.request.SearchRequest;
import com.paytm.digital.education.coaching.consumer.model.response.search.CoachingCenterData;
import com.paytm.digital.education.coaching.consumer.model.response.search.SearchBaseData;
import com.paytm.digital.education.coaching.consumer.model.response.search.SearchResponse;
import com.paytm.digital.education.coaching.consumer.model.response.search.SearchResult;
import com.paytm.digital.education.coaching.consumer.service.search.helper.CoachingSearchAggregateHelper;
import com.paytm.digital.education.database.dao.CoachingInstituteDAO;
import com.paytm.digital.education.es.model.CoachingCenterSearch;
import com.paytm.digital.education.es.model.GeoLocation;
import com.paytm.digital.education.coaching.utils.ImageUtils;
import com.paytm.digital.education.database.entity.CoachingInstituteEntity;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.elasticsearch.models.SortField;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.enums.es.DataSortOrder;
import com.paytm.digital.education.enums.es.FilterQueryType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.CENTER_FILTER_NAMESPACE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.CENTER_SEARCH_NAMESPACE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_CENTER_CITY;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_CENTER_LOCATION;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_CENTER_PLACEHOLDER;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_CENTER_STATE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_COMPONENT;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.DISTANCE_KILOMETERS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.INSTITUTE_COVER_IMAGE_PLACEHOLDER;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.INSTITUTE_ID;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.IS_ENABLED;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.COACHING_CENTER_ADDRESS_1;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.COACHING_CENTER_ADDRESS_1_BOOST;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.COACHING_CENTER_ADDRESS_2;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.COACHING_CENTER_ADDRESS_2_BOOST;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.COACHING_CENTER_ADDRESS_3;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.COACHING_CENTER_ADDRESS_3_BOOST;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.COACHING_CENTER_CITY_ANALYZED;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.COACHING_CENTER_CITY_BOOST;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.COACHING_CENTER_NAME;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.COACHING_CENTER_NAME_BRAND_BOOST;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.COACHING_CENTER_STATE_ANALYZED;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.COACHING_CENTER_STATE_BOOST;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.GLOBAL_PRIORITY;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.SEARCH_ANALYZER_COACHING_CENTER;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.SEARCH_INDEX_COACHING_CENTER;
import static com.paytm.digital.education.constant.CommonConstants.COACHING_CENTER;
import static com.paytm.digital.education.constant.CommonConstants.TOP_COACHING_INSTITUTES_IMAGE;
import static com.paytm.digital.education.enums.es.FilterQueryType.TERMS;

@Slf4j
@Service
@AllArgsConstructor
public class CoachingCenterSearchService extends AbstractSearchService {

    private static DecimalFormat                 df = new DecimalFormat("#.#");
    private static Map<String, Float>            searchFieldKeys;
    private static Map<String, FilterQueryType>  filterQueryTypeMap;
    private        CoachingSearchAggregateHelper coachingSearchAggregateHelper;
    private        CoachingInstituteDAO          coachingInstituteDAO;

    @PostConstruct
    public void init() {
        filterQueryTypeMap = new HashMap<>();
        filterQueryTypeMap.put(INSTITUTE_ID, TERMS);
        filterQueryTypeMap.put(COACHING_CENTER_CITY, TERMS);
        filterQueryTypeMap.put(COACHING_CENTER_STATE, TERMS);
        filterQueryTypeMap.put(IS_ENABLED, TERMS);
        searchFieldKeys = new HashMap<>();
        searchFieldKeys.put(COACHING_CENTER_NAME, COACHING_CENTER_NAME_BRAND_BOOST);
        searchFieldKeys.put(COACHING_CENTER_ADDRESS_1,COACHING_CENTER_ADDRESS_1_BOOST);
        searchFieldKeys.put(COACHING_CENTER_ADDRESS_2,COACHING_CENTER_ADDRESS_2_BOOST);
        searchFieldKeys.put(COACHING_CENTER_ADDRESS_3,COACHING_CENTER_ADDRESS_3_BOOST);
        searchFieldKeys.put(COACHING_CENTER_CITY_ANALYZED,COACHING_CENTER_CITY_BOOST);
        searchFieldKeys.put(COACHING_CENTER_STATE_ANALYZED,COACHING_CENTER_STATE_BOOST);
    }

    @Override
    @Cacheable(value = "coaching_center_search",key = "#searchRequest.key")
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
        buildSearchResponse(searchResponse, elasticResponse, elasticRequest,COACHING_COMPONENT,
                CENTER_FILTER_NAMESPACE,CENTER_SEARCH_NAMESPACE);
        return searchResponse;
    }

    @Override
    protected ElasticRequest buildSearchRequest(SearchRequest searchRequest) {
        ElasticRequest elasticRequest = createSearchRequest(searchRequest,
                SEARCH_ANALYZER_COACHING_CENTER, SEARCH_INDEX_COACHING_CENTER);
        populateSearchFields(searchRequest, elasticRequest, searchFieldKeys,
                CoachingCenterSearch.class);

        Map<String, List<Object>> filters = searchRequest.getFilter();
        if (CollectionUtils.isEmpty(filters)) {
            filters = new HashMap<>();
        }
        filters.put(IS_ENABLED, Collections.singletonList(true));
        searchRequest.setFilter(filters);

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
            LinkedHashMap<String, DataSortOrder> sortKeysInOrder = new LinkedHashMap<>();
            sortKeysInOrder.put(GLOBAL_PRIORITY, DataSortOrder.ASC);
            searchRequest.setSortOrder(sortKeysInOrder);
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
            String instituteImage = null;
            CoachingInstituteEntity coachingInstituteEntity =
                    coachingInstituteDAO.findByInstituteId(
                            Objects.requireNonNull(coachingCenterSearches.get(0).getInstituteId()));
            if (Objects.nonNull(coachingInstituteEntity)) {
                instituteImage = coachingInstituteEntity.getCoverImage();
            }
            searchResults.setEntity(EducationEntity.COACHING_CENTER);
            List<SearchBaseData> coachingCenterDataList = new ArrayList<>();
            for (CoachingCenterSearch coachingCenterSearch : coachingCenterSearches) {
                buildCoachingCenterData(isGeoDistanceSortRequest,
                        indexOfGeoDistanceSortInElasticRequest,
                        coachingCenterDataList, coachingCenterSearch,
                        instituteImage);
            }
            searchResults.setValues(coachingCenterDataList);
        }
        searchResponse.setResults(searchResults);
    }

    private void buildCoachingCenterData(boolean isGeoDistanceSortRequest,
            Integer indexOfGeoDistanceSortInElasticRequest,
            List<SearchBaseData> coachingCenterDataList,
            CoachingCenterSearch coachingCenterSearch, String instituteImage) {
        CoachingCenterData toAdd = CoachingCenterData
                .builder()
                .centerId(coachingCenterSearch.getCenterId())
                .instituteId(coachingCenterSearch.getInstituteId())
                .officialName(coachingCenterSearch.getOfficialName())
                .centerImage(getImageWithAbsolutePath(coachingCenterSearch,instituteImage))
                .openingTime(getFormattedTime(coachingCenterSearch.getOpeningTime(),
                        coachingCenterSearch.getCenterId()))
                .closingTime(getFormattedTime(coachingCenterSearch.getClosingTime(),
                        coachingCenterSearch.getCenterId()))
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

    private String getImageWithAbsolutePath(CoachingCenterSearch coachingCenterSearch,
            String instituteImage) {
        if (Objects.nonNull(coachingCenterSearch.getCenterImage())) {
            return ImageUtils
                    .getImageWithAbsolutePath(coachingCenterSearch.getCenterImage(),
                            COACHING_CENTER_PLACEHOLDER, COACHING_CENTER);
        } else {
            return ImageUtils.getImageWithAbsolutePath(
                    instituteImage, INSTITUTE_COVER_IMAGE_PLACEHOLDER,
                    TOP_COACHING_INSTITUTES_IMAGE);
        }
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

    private String getFormattedTime(String inputTime, Long centerId) {
        String formattedTime;
        if (StringUtils.isEmpty(inputTime)) {
            log.warn("NULL value for time in coaching center : {}", centerId);
            return null;
        }
        String regexp = "^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$";
        if (Pattern.matches(regexp, inputTime)) {
            String [] timeArray = inputTime.split(":");
            int time = Integer.parseInt(timeArray[0]);
            if (time == 0) {
                formattedTime = "12" + ":" + timeArray[1] + " " + "AM";
                return formattedTime;
            }
            if (time == 12) {
                formattedTime = time + ":" + timeArray[1] + " " + "PM";
                return formattedTime;
            }
            if (time < 12) {
                formattedTime = time + ":" + timeArray[1] + " " + "AM";
                return formattedTime;
            }
            if (time > 12) {
                time %= 12;
                formattedTime = time + ":" + timeArray[1] + " " + "PM";
                return formattedTime;
            }

        }
        log.warn("Wrong value for time : {} in coaching center : {}", inputTime, centerId);
        return null;
    }
}
