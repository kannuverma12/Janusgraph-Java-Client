package com.paytm.digital.education.coaching.consumer.service;

import com.paytm.digital.education.coaching.constants.CoachingConstants;
import com.paytm.digital.education.coaching.consumer.model.request.SearchRequest;
import com.paytm.digital.education.coaching.consumer.model.response.search.ExamData;
import com.paytm.digital.education.coaching.consumer.model.response.search.ExamsTopHitsData;
import com.paytm.digital.education.coaching.consumer.model.response.search.SearchBaseData;
import com.paytm.digital.education.coaching.consumer.model.response.search.SearchResponse;
import com.paytm.digital.education.coaching.consumer.model.response.search.SearchResult;
import com.paytm.digital.education.coaching.consumer.service.helper.CoachingSearchAggregateHelper;

import com.paytm.digital.education.coaching.es.model.ExamSearch;
import com.paytm.digital.education.coaching.utils.SearchUtils;
import com.paytm.digital.education.elasticsearch.enums.FilterQueryType;
import com.paytm.digital.education.elasticsearch.models.AggregateField;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.elasticsearch.models.TopHitsAggregationResponse;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.utility.CommonUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING_EXAM_STREAMS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM_PLACEHOLDER;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.EXAM_FULL_NAME;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.EXAM_FULL_NAME_BOOST;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.EXAM_NAME_SYNONYMS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.EXAM_NAME_SYNONYMS_BOOST;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.EXAM_OFFICIAL_NAME;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.EXAM_OFFICIAL_NAME_BOOST;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.EXAM_OFFICIAL_NAME_NGRAM;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.EXAM_OFFICIAL_NAME_NGRAM_BOOST;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.EXAM_SHORT_NAME;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.EXAM_SHORT_NAME_BOOST;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.STREAM_IDS;
import static com.paytm.digital.education.constant.CommonConstants.COACHING_TOP_EXAMS;
import static com.paytm.digital.education.constant.ExploreConstants.LINGUISTIC_MEDIUM;
import static com.paytm.digital.education.constant.ExploreConstants.SEARCH_EXAM_LEVEL;
import static com.paytm.digital.education.elasticsearch.enums.FilterQueryType.TERMS;

@Slf4j
@Service
@AllArgsConstructor
public class ExamSearchService extends AbstractSearchService {

    private        CoachingSearchAggregateHelper coachingSearchAggregateHelper;
    private static Map<String, Float>            searchFieldKeys;
    private static Map<String, FilterQueryType>  filterQueryTypeMap;

    @PostConstruct
    private void init() {
        filterQueryTypeMap = new HashMap<>();
        filterQueryTypeMap.put(STREAM_IDS, TERMS);
        filterQueryTypeMap.put(LINGUISTIC_MEDIUM, TERMS);
        filterQueryTypeMap.put(SEARCH_EXAM_LEVEL, TERMS);
        filterQueryTypeMap.put(COACHING_EXAM_STREAMS, TERMS);
        searchFieldKeys = new HashMap<>();
        searchFieldKeys.put(EXAM_FULL_NAME, EXAM_FULL_NAME_BOOST);
        searchFieldKeys.put(EXAM_SHORT_NAME, EXAM_SHORT_NAME_BOOST);
        searchFieldKeys.put(EXAM_NAME_SYNONYMS, EXAM_NAME_SYNONYMS_BOOST);
        searchFieldKeys.put(EXAM_OFFICIAL_NAME, EXAM_OFFICIAL_NAME_BOOST);
        searchFieldKeys.put(EXAM_OFFICIAL_NAME_NGRAM, EXAM_OFFICIAL_NAME_NGRAM_BOOST);
    }

    @Override
    @Cacheable(value = "exam_search")
    public SearchResponse search(SearchRequest searchRequest) throws IOException, TimeoutException {
        validateRequest(searchRequest, filterQueryTypeMap);
        ElasticRequest elasticRequest = buildSearchRequest(searchRequest);
        ElasticResponse elasticResponse;
        try {
            elasticResponse = initiateSearch(elasticRequest, ExamSearch.class);
        } catch (Exception e) {
            log.error("Error encountered in search query for exam ", e);
            elasticResponse = new ElasticResponse();
        }
        SearchResponse searchResponse = new SearchResponse(searchRequest.getTerm());
        if (searchRequest.isFetchSearchResultsPerFilter()) {
            populateSearchResultPerLevel(searchResponse, elasticResponse);
        }
        buildSearchResponse(searchResponse, elasticResponse, elasticRequest);
        return searchResponse;
    }

    @Override
    protected ElasticRequest buildSearchRequest(SearchRequest searchRequest) {
        ElasticRequest elasticRequest =
                createSearchRequest(searchRequest, CoachingConstants.Search.EXAM_ANALYZER,
                        CoachingConstants.Search.EXAM_INDEX);
        populateSearchFields(searchRequest, elasticRequest, searchFieldKeys, ExamSearch.class);
        populateFilterFields(searchRequest, elasticRequest, ExamSearch.class, filterQueryTypeMap);
        if (StringUtils.isBlank(searchRequest.getTerm())) {
            SearchUtils.setSortKeysInOrder(searchRequest);
        } else {
            searchRequest.setSortOrder(null);
        }
        populateSortFields(searchRequest, elasticRequest, ExamSearch.class);
        AggregateField[] aggregateFields = searchRequest.isFetchSearchResultsPerFilter()
                ? coachingSearchAggregateHelper
                .getTopHitsAggregateData(searchRequest.getDataPerFilter())
                : coachingSearchAggregateHelper.getExamAggregateData();
        populateAggregateFields(searchRequest, elasticRequest, aggregateFields, ExamSearch.class);
        return elasticRequest;
    }

    @Override
    protected void populateSearchResults(SearchResponse searchResponse,
            ElasticResponse elasticResponse, Map<String, Map<String, Object>> properties) {
        List<ExamSearch> examSearches = elasticResponse.getDocuments();
        SearchResult searchResults = new SearchResult();
        if (!CollectionUtils.isEmpty(examSearches)) {
            searchResults.setEntity(EducationEntity.EXAM);
            List<SearchBaseData> examDataList = new ArrayList<SearchBaseData>();
            examSearches.forEach(examSearch -> {
                ExamData examData = new ExamData();
                examData.setExamId(examSearch.getExamId());
                examData.setOfficialName(examSearch.getOfficialName());
                examData.setExamShortName(examSearch.getExamShortName());
                examData.setUrlDisplayKey(
                        CommonUtil.convertNameToUrlDisplayName(examSearch.getOfficialName()));
                List<String> dataAvailable = new ArrayList<>();
                if (!CollectionUtils.isEmpty(examSearch.getDataAvailable())) {
                    dataAvailable.addAll(examSearch.getDataAvailable());
                }
                if (!CollectionUtils.isEmpty(examSearch.getExamInstances())) {
                    int instanceIndex = 0;
                    instanceIndex = SearchUtils.getRelevantInstanceIndex(
                            examSearch.getExamInstances(), CoachingConstants.Search.APPLICATION);
                    SearchUtils.setAllDates(examData,
                            examSearch.getExamInstances().get(instanceIndex));
                    SearchUtils.setExamImportantDates(examData,
                            examSearch.getExamInstances().get(instanceIndex));
                    if (examSearch.getExamInstances().get(instanceIndex)
                            .isSyllabusAvailable()) {
                        dataAvailable.add(CoachingConstants.Search.SYLLABUS_TAB);
                    }
                    dataAvailable.add(CoachingConstants.Search.DATE_TAB);
                }
                examData.setDataAvailable(dataAvailable);

                if (!StringUtils.isBlank(examSearch.getImageLink())) {
                    examData.setLogoUrl(CommonUtil.getAbsoluteUrl(examSearch.getImageLink(),
                            COACHING_TOP_EXAMS));
                } else {
                    examData.setLogoUrl(CommonUtil.getAbsoluteUrl(EXAM_PLACEHOLDER,
                            COACHING_TOP_EXAMS));
                }

                examDataList.add(examData);
            });
            searchResults.setValues(examDataList);
        }
        searchResponse.setResults(searchResults);
    }

    private void populateSearchResultPerLevel(SearchResponse searchResponse,
            ElasticResponse elasticResponse) {
        SearchResult searchResults = new SearchResult();
        if (elasticResponse.getAggregationResponse().containsKey(STREAM_IDS)) {
            TopHitsAggregationResponse<ExamSearch> topHitsAggregationResponse =
                    (TopHitsAggregationResponse<ExamSearch>) elasticResponse
                            .getAggregationResponse().get(STREAM_IDS);
            Map<String, List<ExamData>> examsPerStream = new HashMap<>();
            topHitsAggregationResponse.getDocumentsPerEntity().forEach((key, documents) -> {
                List<ExamData> examDataList = new ArrayList<>();
                documents.forEach(examSearch -> {
                    ExamData examData = new ExamData();
                    examData.setExamId(examSearch.getExamId());
                    examData.setOfficialName(examSearch.getOfficialName());
                    examData.setExamShortName(examSearch.getExamShortName());
                    examData.setUrlDisplayKey(
                            CommonUtil.convertNameToUrlDisplayName(examSearch.getOfficialName()));
                    List<String> dataAvailable = new ArrayList<>();
                    if (!CollectionUtils.isEmpty(examSearch.getDataAvailable())) {
                        dataAvailable.addAll(examSearch.getDataAvailable());
                    }
                    if (!CollectionUtils.isEmpty(examSearch.getExamInstances())) {
                        int instanceIndex = 0;
                        instanceIndex = SearchUtils.getRelevantInstanceIndex(
                                examSearch.getExamInstances(),
                                CoachingConstants.Search.APPLICATION);
                        SearchUtils.setAllDates(examData,
                                examSearch.getExamInstances().get(instanceIndex));
                        SearchUtils.setExamImportantDates(examData,
                                examSearch.getExamInstances().get(instanceIndex));
                        if (examSearch.getExamInstances().get(instanceIndex)
                                .isSyllabusAvailable()) {
                            dataAvailable.add(CoachingConstants.Search.SYLLABUS_TAB);
                        }
                        dataAvailable.add(CoachingConstants.Search.DATE_TAB);
                    }
                    examData.setDataAvailable(dataAvailable);
                    examDataList.add(examData);
                });
                examsPerStream.put(key.getKey(), examDataList);
            });
            ExamsTopHitsData examsTopHitsData = ExamsTopHitsData.builder().examsPerStream(
                    examsPerStream).build();
            List<SearchBaseData> values = new ArrayList<>();
            values.add(examsTopHitsData);
            searchResults.setValues(values);
        }
        searchResponse.setResults(searchResults);
    }
}
