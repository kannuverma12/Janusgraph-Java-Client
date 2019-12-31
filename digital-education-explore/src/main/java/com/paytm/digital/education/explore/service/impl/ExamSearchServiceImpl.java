package com.paytm.digital.education.explore.service.impl;

import static com.paytm.digital.education.constant.ExploreConstants.APPLICATION;
import static com.paytm.digital.education.constant.ExploreConstants.DATE_TAB;
import static com.paytm.digital.education.constant.ExploreConstants.DD_MMM_YYYY;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_FILTER_NAMESPACE;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_FULL_NAME;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_FULL_NAME_BOOST;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_NAME_SYNONYMS;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_NAME_SYNONYMS_BOOST;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_OFFICIAL_NAME;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_OFFICIAL_NAME_BOOST;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_OFFICIAL_NAME_NGRAM;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_OFFICIAL_NAME_NGRAM_BOOST;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_SEARCH_NAMESPACE;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_SHORT_NAME;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_SHORT_NAME_BOOST;
import static com.paytm.digital.education.constant.ExploreConstants.EXPLORE_COMPONENT;
import static com.paytm.digital.education.constant.ExploreConstants.LINGUISTIC_MEDIUM;
import static com.paytm.digital.education.constant.ExploreConstants.MMM_YYYY;
import static com.paytm.digital.education.constant.ExploreConstants.NON_TENTATIVE;
import static com.paytm.digital.education.constant.ExploreConstants.RESULT;
import static com.paytm.digital.education.constant.ExploreConstants.SEARCH_ANALYZER_EXAM;
import static com.paytm.digital.education.constant.ExploreConstants.SEARCH_EXAM_LEVEL;
import static com.paytm.digital.education.constant.ExploreConstants.SEARCH_INDEX_EXAM;
import static com.paytm.digital.education.constant.ExploreConstants.SEARCH_STREAM_PREFIX;
import static com.paytm.digital.education.constant.ExploreConstants.SEARCH_STREAM_SUFFIX;
import static com.paytm.digital.education.constant.ExploreConstants.STREAM_IDS;
import static com.paytm.digital.education.constant.ExploreConstants.SYLLABUS_TAB;
import static com.paytm.digital.education.constant.ExploreConstants.YYYY_MM;
import static com.paytm.digital.education.enums.es.DataSortOrder.ASC;
import static com.paytm.digital.education.enums.es.FilterQueryType.TERMS;

import com.paytm.digital.education.database.entity.ExamPaytmKeys;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.enums.Client;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.enums.es.FilterQueryType;
import com.paytm.digital.education.explore.es.model.Event;
import com.paytm.digital.education.explore.es.model.ExamInstance;
import com.paytm.digital.education.explore.es.model.ExamSearch;
import com.paytm.digital.education.explore.request.dto.search.SearchRequest;
import com.paytm.digital.education.explore.response.dto.search.ExamData;
import com.paytm.digital.education.explore.response.dto.search.SearchBaseData;
import com.paytm.digital.education.explore.response.dto.search.SearchResponse;
import com.paytm.digital.education.explore.response.dto.search.SearchResult;
import com.paytm.digital.education.explore.service.helper.SearchAggregateHelper;
import com.paytm.digital.education.serviceimpl.helper.ExamLogoHelper;
import com.paytm.digital.education.utility.CommonUtil;
import com.paytm.digital.education.utility.DateUtil;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
public class ExamSearchServiceImpl extends AbstractSearchServiceImpl {

    private static Map<String, Float>                   searchFieldKeys;
    private static Map<String, FilterQueryType>         filterQueryTypeMap;
    private static Set<String> sortFields;
    private        SearchAggregateHelper                searchAggregateHelper;
    private        ExamLogoHelper                       examLogoHelper;

    @PostConstruct
    private void init() {
        filterQueryTypeMap = new HashMap<>();
        filterQueryTypeMap.put(LINGUISTIC_MEDIUM, TERMS);
        filterQueryTypeMap.put(SEARCH_EXAM_LEVEL, TERMS);
        filterQueryTypeMap.put(STREAM_IDS, TERMS);
        searchFieldKeys = new HashMap<>();
        searchFieldKeys.put(EXAM_FULL_NAME, EXAM_FULL_NAME_BOOST);
        searchFieldKeys.put(EXAM_SHORT_NAME, EXAM_SHORT_NAME_BOOST);
        searchFieldKeys.put(EXAM_NAME_SYNONYMS, EXAM_NAME_SYNONYMS_BOOST);
        searchFieldKeys.put(EXAM_OFFICIAL_NAME, EXAM_OFFICIAL_NAME_BOOST);
        searchFieldKeys.put(EXAM_OFFICIAL_NAME_NGRAM, EXAM_OFFICIAL_NAME_NGRAM_BOOST);

        sortFields = new HashSet<>();
    }

    @Override
    @Cacheable(value = "exam_search", key = "#searchRequest.cacheKey")
    public SearchResponse search(SearchRequest searchRequest) throws IOException, TimeoutException {
        validateRequest(searchRequest, filterQueryTypeMap);
        ElasticRequest elasticRequest = buildSearchRequest(searchRequest);
        ElasticResponse elasticResponse = initiateSearch(elasticRequest, ExamSearch.class);
        SearchResponse searchResponse = new SearchResponse(searchRequest.getTerm());
        buildSearchResponse(searchResponse,elasticResponse, elasticRequest, EXPLORE_COMPONENT,
                EXAM_FILTER_NAMESPACE, EXAM_SEARCH_NAMESPACE, null, searchRequest.getClient());
        return searchResponse;
    }


    @Override
    protected ElasticRequest buildSearchRequest(SearchRequest searchRequest) {
        ElasticRequest elasticRequest =
                createSearchRequest(searchRequest, SEARCH_ANALYZER_EXAM, SEARCH_INDEX_EXAM);
        populateSearchFields(searchRequest, elasticRequest, searchFieldKeys, ExamSearch.class);
        populateFilterFields(searchRequest, elasticRequest, ExamSearch.class, filterQueryTypeMap);
        populateAggregateFields(searchRequest, elasticRequest,
                searchAggregateHelper.getExamAggregateData(), ExamSearch.class);
        validateSortFields(searchRequest, sortFields);
        setSortOrderByStreamsPosition(searchRequest);
        populateSortFields(searchRequest, elasticRequest, ExamSearch.class);
        return elasticRequest;
    }

    private void setAllDates(ExamData examData, ExamInstance instance) {

        if (!CollectionUtils.isEmpty(instance.getEvents())) {
            instance.getEvents().forEach(event -> {
                if (event.getType().equalsIgnoreCase(APPLICATION)) {
                    if (event.getCertainty() != null
                            && event.getCertainty().equalsIgnoreCase(NON_TENTATIVE)) {
                        if (event.getStartDate() != null) {
                            examData.setApplicationStartDate(
                                    DateUtil.dateToString(event.getStartDate(), DD_MMM_YYYY));
                            examData.setApplicationEndDate(
                                    DateUtil.dateToString(event.getEndDate(), DD_MMM_YYYY));
                        } else {
                            examData.setApplicationStartDate(
                                    DateUtil.dateToString(event.getDate(), DD_MMM_YYYY));
                        }
                    } else {
                        examData.setApplicationMonth(
                                DateUtil.formatDateString(event.getMonth(), YYYY_MM, MMM_YYYY));
                    }
                } else if (event.getType().equalsIgnoreCase(EXAM)) {
                    if (event.getCertainty() != null
                            && event.getCertainty().equalsIgnoreCase(NON_TENTATIVE)) {
                        if (event.getStartDate() != null) {
                            examData.setExamStartDate(
                                    DateUtil.dateToString(event.getStartDate(), DD_MMM_YYYY));
                            examData.setExamEndDate(
                                    DateUtil.dateToString(event.getEndDate(), DD_MMM_YYYY));
                        } else {
                            examData.setExamStartDate(
                                    DateUtil.dateToString(event.getDate(), MMM_YYYY));
                        }
                    } else {
                        examData.setExamMonth(
                                DateUtil.formatDateString(event.getMonth(), YYYY_MM, MMM_YYYY));
                    }
                } else if (event.getType().equalsIgnoreCase(RESULT)) {
                    if (event.getCertainty() != null
                            && event.getCertainty().equalsIgnoreCase(NON_TENTATIVE)) {
                        if (event.getStartDate() != null) {
                            examData.setResultStartDate(
                                    DateUtil.dateToString(event.getStartDate(), DD_MMM_YYYY));
                            examData.setResultEndDate(
                                    DateUtil.dateToString(event.getEndDate(), DD_MMM_YYYY));
                        } else {
                            examData.setResultStartDate(
                                    DateUtil.dateToString(event.getDate(), DD_MMM_YYYY));
                        }
                    } else {
                        examData.setResultMonth(
                                DateUtil.formatDateString(event.getMonth(), YYYY_MM, MMM_YYYY));
                    }
                }
            });
        }
    }

    @Override
    protected void populateSearchResults(SearchResponse searchResponse,
            ElasticResponse elasticResponse, Map<String, Map<String, Object>> properties,
            ElasticRequest elasticRequest,  Client client) {
        List<ExamSearch> examSearches = elasticResponse.getDocuments();
        SearchResult searchResults = new SearchResult();
        Map<Long, SearchBaseData> examDataMap = new HashMap<Long, SearchBaseData>();
        if (!CollectionUtils.isEmpty(examSearches)) {
            searchResults.setEntity(EducationEntity.EXAM);
            List<SearchBaseData> examDataList = new ArrayList<SearchBaseData>();
            examSearches.forEach(examSearch -> {
                ExamData examData = new ExamData();
                examData.setExamId(examSearch.getExamId());
                examData.setOfficialName(examSearch.getOfficialName());
                examData.setUrlDisplayName(
                        CommonUtil.convertNameToUrlDisplayName(examSearch.getOfficialName()));
                examData.setLogoUrl(examLogoHelper
                        .getExamLogoUrl(new Long(examSearch.getExamId()), examSearch.getImageLink()));
                List<String> dataAvailable = new ArrayList<>();
                if (!CollectionUtils.isEmpty(examSearch.getDataAvailable())) {
                    dataAvailable.addAll(examSearch.getDataAvailable());
                }
                if (!CollectionUtils.isEmpty(examSearch.getExamInstances())) {
                    int instanceIndex = 0;
                    instanceIndex = getRelevantInstanceIndex(
                            examSearch.getExamInstances(), APPLICATION);
                    setAllDates(examData, examSearch.getExamInstances().get(instanceIndex));
                    if (examSearch.getExamInstances().get(instanceIndex)
                            .isSyllabusAvailable()) {
                        dataAvailable.add(SYLLABUS_TAB);
                    }
                    dataAvailable.add(DATE_TAB);
                }
                if (Objects.nonNull(examSearch.getPaytmKeys())) {
                    ExamPaytmKeys examPaytmKeys = examSearch.getPaytmKeys();
                    examData.setCollegePredictorPid(examPaytmKeys.getCollegePredictorId());
                    examData.setFormId(examPaytmKeys.getFormId());
                }
                examData.setCtaList(ctaHelper.buildCTA(examData, client));
                examData.setDataAvailable(dataAvailable);
                examDataMap.put(Long.valueOf(examSearch.getExamId()), examData);
                examDataList.add(examData);
            });
            searchResults.setValues(examDataList);
        }
        searchResponse.setResults(searchResults);
        searchResponse.setEntityDataMap(examDataMap);
    }

    private int getRelevantInstanceIndex(List<ExamInstance> instances, String type) {
        int instanceIndex = 0;
        if (!CollectionUtils.isEmpty(instances) || instances.size() > 1) {
            Date presentDate = new Date();
            Date futureMinDate = new Date(Long.MAX_VALUE);
            Date pastMaxDate = new Date(Long.MIN_VALUE);
            for (int index = 0; index < instances.size(); index++) {
                Date minApplicationDate = new Date(Long.MAX_VALUE);
                if (!CollectionUtils.isEmpty(instances.get(index).getEvents())) {
                    List<Event> events = instances.get(index).getEvents();
                    for (int eventIndex = 0; eventIndex < events.size(); eventIndex++) {
                        if (events.get(eventIndex).getType() != null
                                && events.get(eventIndex).getType().equalsIgnoreCase(type)) {
                            if (events.get(eventIndex).getCertainty() != null
                                    && events.get(eventIndex).getCertainty()
                                    .equalsIgnoreCase(NON_TENTATIVE)) {
                                minApplicationDate = events.get(eventIndex).getStartDate() != null
                                        ? events.get(eventIndex).getStartDate()
                                        : events.get(eventIndex).getDate();
                            } else {
                                minApplicationDate =
                                        DateUtil.stringToDate(events.get(eventIndex).getMonth(),
                                                YYYY_MM);
                            }
                        }

                        if (minApplicationDate != null) {
                            if (minApplicationDate.compareTo(presentDate) >= 0
                                    && futureMinDate.compareTo(minApplicationDate) > 0) {
                                futureMinDate = minApplicationDate;
                                instanceIndex = index;
                            } else if (futureMinDate.compareTo(new Date(Long.MAX_VALUE)) == 0
                                    && minApplicationDate.compareTo(pastMaxDate) > 0) {
                                pastMaxDate = minApplicationDate;
                                instanceIndex = index;
                            }
                        }
                    }
                }
            }
        }
        return instanceIndex;
    }

    private void setSortOrderByStreamsPosition(SearchRequest searchRequest) {
        if (searchRequest.getFilter().containsKey(STREAM_IDS) && !CollectionUtils
                .isEmpty(searchRequest.getFilter().get(STREAM_IDS))) {
            if (CollectionUtils.isEmpty(searchRequest.getSortOrder())) {
                searchRequest.setSortOrder(new LinkedHashMap<>());
            }
            List<Object> streamIds = searchRequest.getFilter().get(STREAM_IDS);
            for (Object streamId : streamIds) {
                searchRequest.getSortOrder()
                        .put(SEARCH_STREAM_PREFIX + streamId.toString() + SEARCH_STREAM_SUFFIX, ASC);
            }
        }
    }

}
