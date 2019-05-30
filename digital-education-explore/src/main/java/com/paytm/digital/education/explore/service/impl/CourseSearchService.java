package com.paytm.digital.education.explore.service.impl;

import static com.paytm.digital.education.elasticsearch.enums.FilterQueryType.TERMS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.*;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_INSTITUTE_ID;

import com.paytm.digital.education.elasticsearch.enums.DataSortOrder;
import com.paytm.digital.education.elasticsearch.enums.FilterQueryType;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.explore.database.entity.Institute;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    private        SearchAggregateHelper                searchAggregateHelper;
    private        CommonMongoRepository                commonMongoRepository;
    private        PropertyReader                       propertyReader;
    private        SearchResponseBuilder                searchResponseBuilder;

    @PostConstruct
    private void init() {
        searchFieldKeys = new HashMap<>();
        searchFieldKeys.put(NAME_COURSE, NAME_COURSE_BOOST);
        filterQueryTypeMap = new HashMap<String, FilterQueryType>();
        filterQueryTypeMap.put(BRANCH_COURSE, TERMS);
        filterQueryTypeMap.put(DEGREE_COURSE, TERMS);
        filterQueryTypeMap.put(STREAM_COURSE, TERMS);
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
    }

    @Override
    @Cacheable(value = "course_search")
    public SearchResponse search(SearchRequest searchRequest) throws IOException, TimeoutException {
        CourseSearchResponse courseSearchResponse = new CourseSearchResponse();
        if (searchRequest.getFilter().containsKey(ENTITY_ID)) {
            populatetInstituteDetails(searchRequest, courseSearchResponse);
        }
        validateRequest(searchRequest, filterQueryTypeMap);
        ElasticRequest elasticRequest = buildSearchRequest(searchRequest);
        ElasticResponse elasticResponse = initiateSearch(elasticRequest, CourseSearch.class);
        return buildSearchResponse(elasticResponse, elasticRequest, EXPLORE_COMPONENT,
                COURSE_FILTER_NAMESPACE, courseSearchResponse);
    }

    private SearchResponse buildSearchResponse(ElasticResponse elasticResponse,
            ElasticRequest elasticRequest, String component, String namespace,
            CourseSearchResponse courseSearchResponse) {
        SearchResponse searchResponse = new SearchResponse(elasticRequest.getQueryTerm());
        if (elasticRequest.isSearchRequest()) {
            populateSearchResultsOfCourses(searchResponse, elasticResponse, courseSearchResponse);
            long total = elasticResponse.getTotalSearchResultsCount();
            searchResponse.setTotal(total);
        }
        if (elasticRequest.isAggregationRequest()) {
            Map<String, Map<String, Object>> propertyMap = propertyReader
                    .getPropertiesAsMap(component, namespace);
            searchResponseBuilder
                    .populateSearchFilters(searchResponse, elasticResponse, elasticRequest,
                            propertyMap);
        }
        return searchResponse;
    }

    private void populatetInstituteDetails(SearchRequest searchRequest,
            CourseSearchResponse courseSearchResponse) {
        List<String> fields =
                Arrays.asList(GALLERY_LOGO, INSTITUTION_STATE, INSTITUTION_CITY, INSTITUTE_ID,
                        OFFICIAL_NAME, ENTITY_TYPE);
        Integer instituteId = (Integer) searchRequest.getFilter().get(ENTITY_ID).get(0);
        searchRequest.getFilter().remove(ENTITY_ID);
        Institute institute =
                commonMongoRepository.getEntityByFields(INSTITUTE_ID, instituteId, Institute.class,
                        fields);
        if (institute == null) {
            throw new BadRequestException(INVALID_INSTITUTE_ID,
                    INVALID_INSTITUTE_ID.getExternalMessage());
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
                    .setLogoUrl(CommonUtil.getLogoLink(institute.getGallery().getLogo()));
        }
        courseSearchResponse
                .setOfficialAddress(CommonUtil.getOfficialAddress(institute.getInstitutionState(),
                        institute.getInstitutionCity(), null, null, null));
        courseSearchResponse.setInstituteName(institute.getOfficialName());
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
                searchAggregateHelper.getCourseAggregateData(), CourseSearch.class);
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
                courseData.setOfficialName(courseSearch.getName());
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
            ElasticResponse elasticResponse, Map<String, Map<String, Object>> properties) {
    }
}
