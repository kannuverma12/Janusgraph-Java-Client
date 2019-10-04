package com.paytm.digital.education.coaching.utils;

import com.paytm.digital.education.coaching.constants.CoachingConstants;
import com.paytm.digital.education.coaching.consumer.model.dto.ExamImportantDate;
import com.paytm.digital.education.coaching.consumer.model.request.SearchRequest;
import com.paytm.digital.education.coaching.consumer.model.response.search.ExamData;
import com.paytm.digital.education.coaching.es.model.Event;
import com.paytm.digital.education.coaching.es.model.ExamInstance;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.enums.es.DataSortOrder;
import com.paytm.digital.education.utility.DateUtil;
import lombok.experimental.UtilityClass;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.DD_MMM_YYYY;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EXAM;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.MMM_YYYY;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.NON_TENTATIVE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.APPLICATION;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.EXAM_IDS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.GLOBAL_PRIORITY;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.IGNORE_ENTITY_POSITION;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.RESULT;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.SEARCH_EXAM_PREFIX;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.SEARCH_EXAM_SUFFIX;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.SEARCH_STREAM_PREFIX;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.SEARCH_STREAM_SUFFIX;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.STREAM_IDS;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.YYYY_MM;
import static com.paytm.digital.education.enums.es.DataSortOrder.ASC;

@UtilityClass
public class SearchUtils {

    public void setSortKeysInOrder(SearchRequest searchRequest) {
        if (!CollectionUtils.isEmpty(searchRequest.getSortOrder())) {
            return;
        }
        LinkedHashMap<String, DataSortOrder> sortKeysInOrder = new LinkedHashMap<>();
        if (CollectionUtils.isEmpty(searchRequest.getSortOrder()) || !searchRequest.getSortOrder()
                .containsKey(IGNORE_ENTITY_POSITION)) {
            String keyword = "";
            if (EducationEntity.COACHING_INSTITUTE.equals(searchRequest.getEntity())) {
                keyword = ".keyword";
            }
            if (!CollectionUtils.isEmpty(searchRequest.getFilter())) {
                if (searchRequest.getFilter().containsKey(EXAM_IDS)) {
                    sortKeysInOrder
                            .put(SEARCH_EXAM_PREFIX + searchRequest.getFilter().get(EXAM_IDS).get(0)
                                    + SEARCH_EXAM_SUFFIX + keyword, ASC);
                } else if (searchRequest.getFilter().containsKey(STREAM_IDS)) {
                    sortKeysInOrder
                            .put(SEARCH_STREAM_PREFIX + searchRequest.getFilter().get(STREAM_IDS)
                                    .get(0)
                                    + SEARCH_STREAM_SUFFIX + keyword, ASC);
                }
            }
        }
        if (CollectionUtils.isEmpty(searchRequest.getSortOrder()) || !searchRequest.getSortOrder()
                .containsKey(CoachingConstants.Search.IGNORE_GLOBAL_PRIORITY)) {
            sortKeysInOrder.put(GLOBAL_PRIORITY, ASC);
        }
        searchRequest.setSortOrder(sortKeysInOrder);
    }

    public int getRelevantInstanceIndex(List<ExamInstance> instances, String type) {
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

    public void setAllDates(ExamData examData, ExamInstance instance) {

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

    public void setExamImportantDates(ExamData examData, ExamInstance instance) {
        if (Objects.isNull(instance) || Objects.isNull(instance.getEvents())) {
            return;
        }
        List<Event> events = instance.getEvents();
        List<ExamImportantDate> importantDates = new ArrayList<>();

        for (Event event : events) {
            ExamImportantDate toAdd = ExamImportantDate
                    .builder()
                    .name(examData.getOfficialName())
                    .monthDate(DateUtil.formatDateString(event.getMonth(), YYYY_MM, MMM_YYYY))
                    .monthTimestamp(DateUtil.stringToDate(event.getMonth(), YYYY_MM))
                    .modes(null)
                    .type(event.getType())
                    .typeDisplayName(event.getType())
                    .certainity(event.getCertainty())
                    .build();

            if (event.getStartDate() != null) {
                toAdd.setDateEndRange(event.getEndDate());
                toAdd.setDateStartRange(event.getStartDate());
                toAdd.setDateEndRangeTimestamp(event.getEndDate());
                toAdd.setDateStartRangeTimestamp(event.getStartDate());
            } else {
                toAdd.setDateStartRangeTimestamp(event.getDate());
                toAdd.setDateStartRange(event.getDate());
            }

            importantDates.add(toAdd);
        }
        examData.setImportantDates(importantDates);
    }

}
