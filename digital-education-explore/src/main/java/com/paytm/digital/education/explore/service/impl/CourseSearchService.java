package com.paytm.digital.education.explore.service.impl;

import static com.paytm.digital.education.elasticsearch.enums.FilterQueryType.TERMS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.ACCEPTING_APPLICATION;
import static com.paytm.digital.education.explore.constants.ExploreConstants.BRANCH_COURSE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.COURSE_ALPHABETICAL_SORT_KEY;
import static com.paytm.digital.education.explore.constants.ExploreConstants.COURSE_FILTER_NAMESPACE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.COURSE_LEVEL;
import static com.paytm.digital.education.explore.constants.ExploreConstants.DEGREE_COURSE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.DURATION_COURSE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.ENTITY_ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.ENTITY_NAME;
import static com.paytm.digital.education.explore.constants.ExploreConstants.ENTITY_TYPE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXPLORE_COMPONENT;
import static com.paytm.digital.education.explore.constants.ExploreConstants.FEE_COURSE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.GALLERY_LOGO;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_ID_COURSE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_NAME_COURSE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTION_CITY;
import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTION_STATE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.IS_CLIENT;
import static com.paytm.digital.education.explore.constants.ExploreConstants.LEVEL_COURSE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.NAME_COURSE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.NAME_COURSE_BOOST;
import static com.paytm.digital.education.explore.constants.ExploreConstants.NAME_COURSE_SEARCH;
import static com.paytm.digital.education.explore.constants.ExploreConstants.OFFICIAL_NAME;
import static com.paytm.digital.education.explore.constants.ExploreConstants.PARENT_INSTITUTE_ID_COURSE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SEARCH_ANALYZER_COURSE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SEARCH_INDEX_COURSE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SEATS_COURSE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.STREAM_IDS;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_INSTITUTE_ID;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_INSTITUTE_NAME;

import com.paytm.digital.education.elasticsearch.enums.DataSortOrder;
import com.paytm.digital.education.elasticsearch.enums.FilterQueryType;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.elasticsearch.models.TopHitsAggregationResponse;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.explore.constants.ExploreConstants;
import com.paytm.digital.education.explore.database.entity.Institute;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.enums.Client;
import com.paytm.digital.education.explore.enums.CollegeEntityType;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.es.model.CourseSearch;
import com.paytm.digital.education.explore.request.dto.search.SearchRequest;
import com.paytm.digital.education.explore.response.builders.SearchResponseBuilder;
import com.paytm.digital.education.explore.response.dto.search.CourseData;
import com.paytm.digital.education.explore.response.dto.search.CourseSearchResponse;
import com.paytm.digital.education.explore.response.dto.search.SearchBaseData;
import com.paytm.digital.education.explore.response.dto.search.SearchResponse;
import com.paytm.digital.education.explore.response.dto.search.SearchResult;
import com.paytm.digital.education.explore.service.helper.SearchAggregateHelper;
import com.paytm.digital.education.explore.utility.CommonUtil;
import com.paytm.digital.education.property.reader.PropertyReader;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import javax.annotation.PostConstruct;

@Service
@AllArgsConstructor
public class CourseSearchService extends AbstractSearchServiceImpl {

    private static Map<String, FilterQueryType>         filterQueryTypeMap;
    private static Map<String, Float>                   searchFieldKeys;
    private static LinkedHashMap<String, DataSortOrder> defaultSortKeysInOrder;
    private static LinkedHashMap<String, DataSortOrder> alphabeticalSortKeysAsc;
    private static LinkedHashMap<String, DataSortOrder> alphabeticalSortKeysDesc;
    private static Set<String>                          allowedSortFields;
    private        SearchAggregateHelper                searchAggregateHelper;
    private        CommonMongoRepository                commonMongoRepository;
    private        PropertyReader                       propertyReader;
    private        SearchResponseBuilder                searchResponseBuilder;

    @PostConstruct
    private void init() {
        searchFieldKeys = new HashMap<>();
        searchFieldKeys.put(NAME_COURSE_SEARCH, NAME_COURSE_BOOST);
        filterQueryTypeMap = new HashMap<String, FilterQueryType>();
        filterQueryTypeMap.put(BRANCH_COURSE, TERMS);
        filterQueryTypeMap.put(DEGREE_COURSE, TERMS);
        filterQueryTypeMap.put(STREAM_IDS, TERMS);
        filterQueryTypeMap.put(INSTITUTE_NAME_COURSE, TERMS);
        filterQueryTypeMap.put(LEVEL_COURSE, TERMS);
        filterQueryTypeMap.put(ACCEPTING_APPLICATION, TERMS);
        filterQueryTypeMap.put(PARENT_INSTITUTE_ID_COURSE, TERMS);
        filterQueryTypeMap.put(INSTITUTE_ID_COURSE, TERMS);
        defaultSortKeysInOrder = new LinkedHashMap<>();
        defaultSortKeysInOrder.put(SEATS_COURSE, DataSortOrder.DESC);
        defaultSortKeysInOrder.put(DURATION_COURSE, DataSortOrder.ASC);
        defaultSortKeysInOrder.put(FEE_COURSE, DataSortOrder.ASC);
        alphabeticalSortKeysAsc = new LinkedHashMap<>();
        alphabeticalSortKeysAsc.put(NAME_COURSE, DataSortOrder.ASC);
        alphabeticalSortKeysDesc = new LinkedHashMap<>();
        alphabeticalSortKeysDesc.put(NAME_COURSE, DataSortOrder.DESC);

        allowedSortFields = new HashSet<>(defaultSortKeysInOrder.keySet());
        allowedSortFields.addAll(alphabeticalSortKeysAsc.keySet());
        allowedSortFields.addAll(alphabeticalSortKeysDesc.keySet());
        allowedSortFields.add(COURSE_ALPHABETICAL_SORT_KEY);
    }

    @Override
    @Cacheable(value = "course_search")
    public SearchResponse search(SearchRequest searchRequest) throws IOException, TimeoutException {
        CourseSearchResponse courseSearchResponse = new CourseSearchResponse();
        /*
         * Filter map containing entity_id means this request is coming from outer world*/
        if (searchRequest.getFilter().containsKey(ENTITY_ID)) {
            populatetInstituteDetails(searchRequest, courseSearchResponse);
        }
        validateRequest(searchRequest, filterQueryTypeMap);
        ElasticRequest elasticRequest = buildSearchRequest(searchRequest);
        ElasticResponse elasticResponse = initiateSearch(elasticRequest, CourseSearch.class);
        return buildSearchResponse(elasticResponse, elasticRequest, EXPLORE_COMPONENT,
                COURSE_FILTER_NAMESPACE, courseSearchResponse, searchRequest.getClient());
    }

    private SearchResponse buildSearchResponse(ElasticResponse elasticResponse,
            ElasticRequest elasticRequest, String component, String namespace,
            CourseSearchResponse courseSearchResponse, Client client) {
        SearchResponse searchResponse = new SearchResponse(elasticRequest.getQueryTerm());
        if (elasticRequest.isSearchRequest()) {
            populateSearchResultsOfCourses(searchResponse, elasticResponse,
                    courseSearchResponse);
            long total = elasticResponse.getTotalSearchResultsCount();
            searchResponse.setTotal(total);
        }
        if (elasticRequest.isAggregationRequest()) {
            if (Client.APP.equals(client)) {
                populateSearchResultPerLevel(searchResponse, elasticResponse,
                        courseSearchResponse);
            } else {
                Map<String, Map<String, Object>> propertyMap = propertyReader
                        .getPropertiesAsMap(component, namespace);
                searchResponseBuilder
                        .populateSearchFilters(searchResponse, elasticResponse, elasticRequest,
                                propertyMap);
            }
        }
        return searchResponse;
    }

    private void populatetInstituteDetails(SearchRequest searchRequest,
            CourseSearchResponse courseSearchResponse) {
        List<String> fields =
                Arrays.asList(GALLERY_LOGO, INSTITUTION_STATE, INSTITUTION_CITY, INSTITUTE_ID,
                        OFFICIAL_NAME, ENTITY_TYPE, IS_CLIENT);

        Integer instituteId = (Integer) searchRequest.getFilter().get(ENTITY_ID).get(0);
        searchRequest.getFilter().remove(ENTITY_ID);
        String instituteUrlKey = (String) searchRequest.getFilter().get(ENTITY_NAME).get(0);
        searchRequest.getFilter().remove(ENTITY_NAME);
        Institute institute =
                commonMongoRepository.getEntityByFields(INSTITUTE_ID, instituteId, Institute.class,
                        fields);
        if (institute == null) {
            throw new BadRequestException(INVALID_INSTITUTE_ID,
                    INVALID_INSTITUTE_ID.getExternalMessage());
        }
        /*
         * null institute URL means that this is an internal request.
         * There is a validator for all other requests.
         * */
        if (Objects.nonNull(instituteUrlKey) && !instituteUrlKey
                .equals(CommonUtil.convertNameToUrlDisplayName(institute.getOfficialName()))) {
            throw new BadRequestException(INVALID_INSTITUTE_NAME,
                    INVALID_INSTITUTE_NAME.getExternalMessage());
        }
        String filterField;
        if (institute.getEntityType().equals(CollegeEntityType.UNIVERSITY)) {
            filterField = PARENT_INSTITUTE_ID_COURSE;
        } else {
            filterField = INSTITUTE_ID;
        }
        searchRequest.getFilter().put(filterField, Arrays.asList(instituteId));
        if (institute.getGallery() != null && StringUtils
                .isNotBlank(institute.getGallery().getLogo())) {
            courseSearchResponse
                    .setLogoUrl(CommonUtil.getLogoLink(institute.getGallery().getLogo(),
                            EducationEntity.INSTITUTE));
        }
        courseSearchResponse
                .setOfficialAddress(CommonUtil.getOfficialAddress(institute.getInstitutionState(),
                        institute.getInstitutionCity(), null, null,
                        null));
        courseSearchResponse.setInstituteName(institute.getOfficialName());
        courseSearchResponse.setUrlDisplayName(
                CommonUtil.convertNameToUrlDisplayName(institute.getOfficialName()));
        if (institute.getIsClient() == 1) {
            courseSearchResponse.setClient(true);
        }
        courseSearchResponse.setInstituteId(institute.getInstituteId());
    }

    @Override
    protected ElasticRequest buildSearchRequest(SearchRequest searchRequest) {
        ElasticRequest elasticRequest =
                createSearchRequest(searchRequest, SEARCH_ANALYZER_COURSE, SEARCH_INDEX_COURSE);
        populateSearchFields(searchRequest, elasticRequest, searchFieldKeys,
                CourseSearch.class);
        populateFilterFields(searchRequest, elasticRequest, CourseSearch.class,
                filterQueryTypeMap);
        populateAggregateFields(searchRequest, elasticRequest,
                searchAggregateHelper.getCourseAggregateData(searchRequest.getClient()),
                CourseSearch.class);

        validateSortFields(searchRequest, allowedSortFields);
        LinkedHashMap<String, DataSortOrder> sortOrder = new LinkedHashMap<>();
        if (!CollectionUtils.isEmpty(searchRequest.getSortOrder())) {
            if (searchRequest.getSortOrder().containsKey(COURSE_ALPHABETICAL_SORT_KEY)) {
                if (searchRequest.getSortOrder().get(COURSE_ALPHABETICAL_SORT_KEY)
                        .equals(DataSortOrder.ASC)) {
                    sortOrder.putAll(alphabeticalSortKeysAsc);
                } else {
                    sortOrder.putAll(alphabeticalSortKeysDesc);
                }
            }
        }
        sortOrder.putAll(defaultSortKeysInOrder);
        searchRequest.setSortOrder(sortOrder);
        populateSortFields(searchRequest, elasticRequest, CourseSearch.class);
        return elasticRequest;
    }

    private void populateSearchResultPerLevel(SearchResponse searchResponse,
            ElasticResponse elasticResponse, CourseSearchResponse courseSearchResponse) {
        SearchResult searchResults = new SearchResult();
        if (elasticResponse.getAggregationResponse().containsKey(ExploreConstants.COURSE_LEVEL)) {
            TopHitsAggregationResponse<CourseSearch> topHitsAggregationResponse =
                    (TopHitsAggregationResponse<CourseSearch>) elasticResponse
                            .getAggregationResponse().get(COURSE_LEVEL);
            Map<String, List<CourseData>> courseDataPerLevel = new HashMap<>();
            topHitsAggregationResponse.getDocumentsPerEntity().forEach((key, documents) -> {
                List<CourseData> courseDataList = new ArrayList<>();
                documents.forEach(courseSearch -> {
                    CourseData courseData = new CourseData();
                    courseData.setCourseId(courseSearch.getCourseId());
                    courseData.setDegrees(courseSearch.getDegree());
                    courseData.setDurationInMonths(courseSearch.getDurationInMonths());
                    courseData.setFee(courseSearch.getFees());
                    courseData.setSeatsAvailable(courseSearch.getSeats());
                    courseData.setStream(courseSearch.getDomainName());
                    courseData.setOfficialName(courseSearch.getName());
                    courseData.setUrlDisplayKey(
                            CommonUtil.convertNameToUrlDisplayName(courseSearch.getName()));
                    courseData.setInstituteName(courseSearch.getInstituteName());
                    courseDataList.add(courseData);
                });
                courseDataPerLevel.put(key.getKey(), courseDataList);
            });
            courseSearchResponse.setCoursesPerLevel(courseDataPerLevel);
            List<SearchBaseData> values = new ArrayList<>();
            values.add(courseSearchResponse);
            searchResults.setValues(values);
            Map<Long, SearchBaseData> searchBaseDataMap = new HashMap<>();
            searchBaseDataMap.put(courseSearchResponse.getInstituteId(), values.get(0));
            searchResponse.setEntityDataMap(searchBaseDataMap);
        }
        searchResponse.setResults(searchResults);
    }

    protected void populateSearchResultsOfCourses(SearchResponse searchResponse,
            ElasticResponse elasticResponse, CourseSearchResponse courseSearchResponse) {
        List<CourseSearch> courseSearches = elasticResponse.getDocuments();
        SearchResult searchResults = new SearchResult();
        if (!CollectionUtils.isEmpty(courseSearches)) {
            searchResults.setEntity(EducationEntity.COURSE);
            List<CourseData> courseDataList = new ArrayList<CourseData>();
            for (CourseSearch courseSearch : courseSearches) {
                CourseData courseData = new CourseData();
                courseData.setCourseId(courseSearch.getCourseId());
                courseData.setDegrees(courseSearch.getDegree());
                courseData.setDurationInMonths(courseSearch.getDurationInMonths());
                courseData.setFee(courseSearch.getFees());
                courseData.setSeatsAvailable(courseSearch.getSeats());
                courseData.setStream(courseSearch.getDomainName());
                courseData.setOfficialName(courseSearch.getName());
                courseData.setUrlDisplayKey(
                        CommonUtil.convertNameToUrlDisplayName(courseSearch.getName()));
                courseData.setInstituteName(courseSearch.getInstituteName());
                courseDataList.add(courseData);
            }
            courseSearchResponse.setCourses(courseDataList);
            List<SearchBaseData> values = new ArrayList<>();
            values.add(courseSearchResponse);
            searchResults.setValues(values);
            Map<Long, SearchBaseData> searchBaseDataMap = new HashMap<>();
            searchBaseDataMap.put(courseSearchResponse.getInstituteId(), values.get(0));
            searchResponse.setEntityDataMap(searchBaseDataMap);
        }
        searchResponse.setResults(searchResults);
    }

    @Override
    protected void populateSearchResults(SearchResponse searchResponse,
            ElasticResponse elasticResponse, Map<String, Map<String, Object>> properties,
            ElasticRequest elasticRequest, Client client) {
    }
}
