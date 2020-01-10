package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.repository.CommonEntityMongoDAO;
import com.paytm.digital.education.dto.detail.Event;
import com.paytm.digital.education.dto.detail.ImportantDate;
import com.paytm.digital.education.explore.response.dto.search.ExamData;
import com.paytm.digital.education.serviceimpl.helper.ExamDatesHelper;
import com.paytm.digital.education.utility.DateUtil;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.paytm.digital.education.constant.ExploreConstants.APPLICATION;
import static com.paytm.digital.education.constant.ExploreConstants.DD_MMM_YYYY;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_FULL_NAME;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_ID;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_SHORT_NAME;
import static com.paytm.digital.education.constant.ExploreConstants.INSTANCES;
import static com.paytm.digital.education.constant.ExploreConstants.LOGO;
import static com.paytm.digital.education.constant.ExploreConstants.MMM_YYYY;
import static com.paytm.digital.education.constant.ExploreConstants.NON_TENTATIVE;
import static com.paytm.digital.education.constant.ExploreConstants.RESULT;
import static com.paytm.digital.education.constant.ExploreConstants.SUB_EXAMS;

@Component
@RequiredArgsConstructor
public class ExamSearchDatesHelper {
    private static final Logger               log                  =
            LoggerFactory.getLogger(ExamSearchDatesHelper.class);
    private static       List<String>         examProjectionFields =
            Arrays.asList(EXAM_ID, EXAM_FULL_NAME, EXAM_SHORT_NAME, LOGO, INSTANCES, SUB_EXAMS);
    private final        CommonEntityMongoDAO commonEntityMongoDAO;
    private final        ExamDatesHelper      examDatesHelper;
    @Value("${exam.instances.for.date.landing.page:1}")
    private              Integer              noOfInstances;

    void setAllImportantDates(ExamData examData) {
        try {
            Exam exam = commonEntityMongoDAO
                    .getExamById(((Integer) examData.getExamId()).longValue(),
                            examProjectionFields);
            if (Objects.isNull(exam)) {
                return;
            }
            List<ImportantDate> importantDates =
                    examDatesHelper.getImportantDates(exam, noOfInstances);
            if (!CollectionUtils.isEmpty(importantDates)) {
                ImportantDate importantDate = importantDates.get(0);
                if (!CollectionUtils.isEmpty(importantDate.getUpcomingDates())) {
                    for (com.paytm.digital.education.dto.detail.Event event : importantDate
                            .getUpcomingDates()) {
                        updateNearestUpcomingOrOngoingDates(examData, event);
                    }
                }
            }
        } catch (Exception ex) {
            log.error(
                    "Error caught while calculating important dates of exams search. ExamId : {} ",
                    ex, examData.getExamId());
        }
    }

    private void updateNearestUpcomingOrOngoingDates(ExamData examData,
            com.paytm.digital.education.dto.detail.Event event) {
        if (examDatesHelper.isOngoingOrUpComingEvent(event)) {
            if (event.getType().equalsIgnoreCase(APPLICATION)) {
                updateApplicationDates(examData, event);
            } else if (event.getType().equalsIgnoreCase(EXAM)) {
                updateExamDates(examData, event);
            } else if (event.getType().equalsIgnoreCase(RESULT)) {
                updateResultDates(examData, event);
            }
        }
    }

    private void updateResultDates(ExamData examData, Event event) {
        if (isNonTentativeDate(event)) {
            if (Objects.nonNull(event.getDateStartRange())) {
                examData.setResultStartDate(
                        DateUtil.dateToString(event.getDateStartRange(), DD_MMM_YYYY));
                examData.setResultEndDate(
                        DateUtil.dateToString(event.getDateEndRange(), DD_MMM_YYYY));
            } else {
                examData.setResultStartDate(
                        DateUtil.dateToString(event.getDateStartRange(), DD_MMM_YYYY));
            }
        } else {
            examData.setResultMonth(event.getMonthDate());
        }
    }

    private void updateExamDates(ExamData examData, Event event) {
        if (isNonTentativeDate(event)) {
            if (Objects.nonNull(event.getDateStartRange())) {
                examData.setExamStartDate(
                        DateUtil.dateToString(event.getDateStartRange(), DD_MMM_YYYY));
                examData.setExamEndDate(
                        DateUtil.dateToString(event.getDateEndRange(), DD_MMM_YYYY));
            } else {
                examData.setExamStartDate(
                        DateUtil.dateToString(event.getDateStartRange(), MMM_YYYY));
            }
        } else {
            examData.setExamMonth(event.getMonthDate());
        }
    }

    private void updateApplicationDates(ExamData examData, Event event) {
        if (isNonTentativeDate(event)) {
            if (Objects.nonNull(event.getDateStartRange())) {
                examData.setApplicationStartDate(
                        DateUtil.dateToString(event.getDateStartRange(), DD_MMM_YYYY));
                examData.setApplicationEndDate(
                        DateUtil.dateToString(event.getDateEndRange(), DD_MMM_YYYY));
            } else {
                examData.setApplicationStartDate(
                        DateUtil.dateToString(event.getDateStartRange(), DD_MMM_YYYY));
            }
        } else {
            examData.setApplicationMonth(event.getMonthDate());
        }
    }

    private boolean isNonTentativeDate(Event event) {
        return Objects.nonNull(event.getCertainity())
                && event.getCertainity().equalsIgnoreCase(NON_TENTATIVE);
    }
}
