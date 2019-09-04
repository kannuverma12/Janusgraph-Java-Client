package com.paytm.digital.education.coaching.consumer.service.utils;

import com.paytm.digital.education.coaching.consumer.model.dto.ExamImportantDate;
import com.paytm.digital.education.database.entity.Event;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.Instance;
import com.paytm.digital.education.utility.DateUtil;
import lombok.experimental.UtilityClass;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.MMM_YYYY;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.NON_TENTATIVE;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.Search.APPLICATION;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.YYYY_MM;
import static com.paytm.digital.education.utility.DateUtil.stringToDate;

@UtilityClass
public class CommonServiceUtils {

    private static final Date MAX_DATE = new Date(Long.MAX_VALUE);
    private static final Date MIN_DATE = new Date(Long.MIN_VALUE);

    public static List<ExamImportantDate> buildExamImportantDates(Exam exam) {
        List<ExamImportantDate> importantDates = new ArrayList<>();
        if (CollectionUtils.isEmpty(exam.getInstances())) {
            return importantDates;
        }

        int relevantInstanceIndex = getRelevantInstanceIndex(exam.getInstances(), APPLICATION);

        importantDates.addAll(convertEventEntityToResponse(
                exam.getExamFullName(),
                exam.getInstances().get(relevantInstanceIndex).getEvents()));

        return importantDates;
    }

    private int getRelevantInstanceIndex(List<Instance> instances, String type) {
        int instanceIndex = 0;
        Date presentDate = new Date();
        Date futureMinDate = MAX_DATE;
        Date pastMaxDate = MIN_DATE;

        for (int index = 0; index < instances.size(); index++) {
            Date minApplicationDate = MAX_DATE;
            if (!CollectionUtils.isEmpty(instances.get(index).getEvents())) {
                List<Event> events = instances.get(index).getEvents();

                for (Event event : events) {
                    if (event.getType().equalsIgnoreCase(type)) {
                        Date eventDate;
                        if (NON_TENTATIVE.equalsIgnoreCase(event.getCertainty())) {
                            eventDate = event.getDate() != null
                                    ? event.getDate()
                                    : event.getDateRangeStart();
                        } else {
                            eventDate = stringToDate(event.getMonthDate(), YYYY_MM);
                        }

                        if (eventDate != null && minApplicationDate.after(eventDate)) {
                            minApplicationDate = eventDate;
                        }
                        if (eventDate != null && minApplicationDate.after(presentDate)
                                && futureMinDate.after(eventDate)) {
                            futureMinDate = minApplicationDate;
                            instanceIndex = index;
                        } else if (futureMinDate.equals(MAX_DATE)
                                && minApplicationDate.after(pastMaxDate)) {
                            pastMaxDate = minApplicationDate;
                            instanceIndex = index;
                        }
                    }
                }
            }
        }
        return instanceIndex;
    }

    private List<ExamImportantDate> convertEventEntityToResponse(String examName,
            List<Event> events) {
        List<ExamImportantDate> response = new ArrayList<>();

        if (!CollectionUtils.isEmpty(events)) {
            for (Event event : events) {
                ExamImportantDate toAdd = ExamImportantDate
                        .builder()
                        .name(examName)
                        .monthDate(DateUtil.formatDateString(
                                event.getMonthDate(), YYYY_MM, MMM_YYYY))
                        .monthTimestamp(DateUtil.stringToDate(event.getMonthDate(), YYYY_MM))
                        .modes(event.getModes())
                        .type(event.getType())
                        .typeDisplayName(event.getType())
                        .certainity(event.getCertainty())
                        .build();

                if (event.getDateRangeStart() != null) {
                    toAdd.setDateEndRange(event.getDateRangeEnd());
                    toAdd.setDateStartRange(event.getDateRangeStart());
                    toAdd.setDateEndRangeTimestamp(event.getDateRangeEnd());
                    toAdd.setDateStartRangeTimestamp(event.getDateRangeStart());
                } else {
                    toAdd.setDateStartRangeTimestamp(event.getDate());
                    toAdd.setDateStartRange(event.getDate());
                }
                response.add(toAdd);
            }
        }
        return response;
    }

}
