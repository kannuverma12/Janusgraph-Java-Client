package com.paytm.digital.education.explore.service.impl;

import static com.paytm.digital.education.elasticsearch.enums.FilterQueryType.TERMS;
import static com.paytm.digital.education.explore.constants.ExploreConstants.APPLICATION;
import static com.paytm.digital.education.explore.constants.ExploreConstants.DATE_TAB;
import static com.paytm.digital.education.explore.constants.ExploreConstants.DD_MMMM_YYYY;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM;
import static com.paytm.digital.education.explore.constants.ExploreConstants.LINGUISTIC_MEDIUM;
import static com.paytm.digital.education.explore.constants.ExploreConstants.MMMM_YYYY;
import static com.paytm.digital.education.explore.constants.ExploreConstants.NON_TENTATIVE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.RESULT;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SEARCH_ANALYZER_EXAM;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SEARCH_EXAM_LEVEL;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SEARCH_INDEX_EXAM;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SEARCH_NAMES_EXAM;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SYLLABUS_TAB;
import static com.paytm.digital.education.explore.constants.ExploreConstants.YYYY_MM;
import com.paytm.digital.education.elasticsearch.enums.AggregationType;
import com.paytm.digital.education.elasticsearch.enums.FilterQueryType;
import com.paytm.digital.education.elasticsearch.models.AggregateField;
import com.paytm.digital.education.elasticsearch.models.ElasticRequest;
import com.paytm.digital.education.elasticsearch.models.ElasticResponse;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.es.model.Event;
import com.paytm.digital.education.explore.es.model.ExamInstance;
import com.paytm.digital.education.explore.es.model.ExamSearch;
import com.paytm.digital.education.explore.request.dto.search.SearchRequest;
import com.paytm.digital.education.explore.response.dto.search.ExamData;
import com.paytm.digital.education.explore.response.dto.search.SearchBaseData;
import com.paytm.digital.education.explore.response.dto.search.SearchResponse;
import com.paytm.digital.education.explore.response.dto.search.SearchResult;
import com.paytm.digital.education.explore.service.helper.ExamInstanceHelper;
import com.paytm.digital.education.explore.service.helper.SearchAggregateHelper;
import com.paytm.digital.education.utility.DateUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import javax.annotation.PostConstruct;

@Slf4j
@Service
@AllArgsConstructor
public class ExamSearchServiceImpl extends AbstractSearchServiceImpl {

    private static List<String>                 searchFieldKeys;
    private static Map<String, FilterQueryType> filterQueryTypeMap;
    private static List<String>                 sortKeysInOrder;

    @PostConstruct
    private void init() {
        filterQueryTypeMap = new HashMap<String, FilterQueryType>();
        filterQueryTypeMap.put(LINGUISTIC_MEDIUM, TERMS);
        filterQueryTypeMap.put(SEARCH_EXAM_LEVEL, TERMS);
        searchFieldKeys = Arrays.asList(SEARCH_NAMES_EXAM);
    }

    @Override
    public SearchResponse search(SearchRequest searchRequest) throws IOException, TimeoutException {
        validateRequest(searchRequest, filterQueryTypeMap);
        ElasticRequest elasticRequest = buildSearchRequest(searchRequest);
        ElasticResponse elasticResponse = initiateSearch(elasticRequest, ExamSearch.class);
        return buildSearchResponse(elasticResponse, elasticRequest);
    }


    @Override
    protected ElasticRequest buildSearchRequest(SearchRequest searchRequest) {
        ElasticRequest elasticRequest =
                createSearchRequest(searchRequest, SEARCH_ANALYZER_EXAM, SEARCH_INDEX_EXAM);
        populateSearchFields(searchRequest, elasticRequest, searchFieldKeys, ExamSearch.class);
        populateFilterFields(searchRequest, elasticRequest, ExamSearch.class, filterQueryTypeMap);
        populateAggregateFields(searchRequest, elasticRequest);
        populateSortFields(searchRequest, elasticRequest, ExamSearch.class, sortKeysInOrder);
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
                                    DateUtil.dateToString(event.getStartDate(), DD_MMMM_YYYY));
                            examData.setApplicationEndDate(
                                    DateUtil.dateToString(event.getEndDate(), DD_MMMM_YYYY));
                        } else {
                            examData.setApplicationStartDate(
                                    DateUtil.dateToString(event.getDate(), DD_MMMM_YYYY));
                        }
                    } else {
                        examData.setApplicationMonth(
                                DateUtil.formatDateString(event.getMonth(), YYYY_MM, MMMM_YYYY));
                    }
                } else if (event.getType().equalsIgnoreCase(EXAM)) {
                    if (event.getCertainty() != null
                            && event.getCertainty().equalsIgnoreCase(NON_TENTATIVE)) {
                        if (event.getStartDate() != null) {
                            examData.setExamStartDate(
                                    DateUtil.dateToString(event.getStartDate(), DD_MMMM_YYYY));
                            examData.setExamEndDate(
                                    DateUtil.dateToString(event.getEndDate(), DD_MMMM_YYYY));
                        } else {
                            examData.setExamStartDate(
                                    DateUtil.dateToString(event.getDate(), MMMM_YYYY));
                        }
                    } else {
                        examData.setExamMonth(
                                DateUtil.formatDateString(event.getMonth(), YYYY_MM, MMMM_YYYY));
                    }
                } else if (event.getType().equalsIgnoreCase(RESULT)) {
                    if (event.getCertainty() != null
                            && event.getCertainty().equalsIgnoreCase(NON_TENTATIVE)) {
                        if (event.getStartDate() != null) {
                            examData.setResultStartDate(
                                    DateUtil.dateToString(event.getStartDate(), DD_MMMM_YYYY));
                            examData.setResultEndDate(
                                    DateUtil.dateToString(event.getEndDate(), DD_MMMM_YYYY));
                        } else {
                            examData.setResultStartDate(
                                    DateUtil.dateToString(event.getDate(), DD_MMMM_YYYY));
                        }
                    } else {
                        examData.setResultMonth(
                                DateUtil.formatDateString(event.getMonth(), YYYY_MM, MMMM_YYYY));
                    }
                }
            });
        }
    }

    @Override
    protected void populateSearchResults(SearchResponse searchResponse,
            ElasticResponse elasticResponse) {
        List<ExamSearch> examSearches = elasticResponse.getDocuments();
        SearchResult searchResults = new SearchResult();
        if (!CollectionUtils.isEmpty(examSearches)) {
            searchResults.setEntity(EducationEntity.EXAM);
            List<SearchBaseData> examDataList = new ArrayList<SearchBaseData>();
            examSearches.forEach(examSearch -> {
                ExamData examData = new ExamData();
                examData.setExamId(examSearch.getExamId());
                examData.setOfficialName(examSearch.getOfficialName());
                examData.setLogoUrl(examSearch.getLogoUrl());
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
                examData.setDataAvailable(dataAvailable);
                examDataList.add(examData);
            });
            searchResults.setValues(examDataList);
        }
        searchResponse.setResults(searchResults);
    }

    @Override
    protected void populateAggregateFields(SearchRequest searchRequest,
            ElasticRequest elasticRequest) {
        if (searchRequest.getFetchFilter()) {
            AggregateField[] aggregateFields = SearchAggregateHelper.getExamAggregateData();
            Map<String, List<Object>> filters = searchRequest.getFilter();
            for (int i = 0; i < aggregateFields.length; i++) {
                aggregateFields[i].setPath(
                        hierarchyMap.get(ExamSearch.class).get(aggregateFields[i].getName()));
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
                                && events.get(eventIndex).getType().equalsIgnoreCase(type)
                                && (events.get(eventIndex).getDate() != null
                                        || events.get(eventIndex).getStartDate() != null
                                        || events.get(eventIndex).getMonth() != null)) {
                            Date eventDate;
                            if (events.get(eventIndex).getCertainty() != null
                                    && events.get(eventIndex).getCertainty()
                                            .equalsIgnoreCase(NON_TENTATIVE)) {
                                eventDate = events.get(eventIndex).getStartDate() != null
                                        ? events.get(eventIndex).getStartDate()
                                        : events.get(eventIndex).getDate();
                            } else {
                                eventDate = DateUtil.stringToDate(events.get(eventIndex).getMonth(),
                                        YYYY_MM);
                            }

                            minApplicationDate = eventDate;
                        }
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
        return instanceIndex;
    }

}
