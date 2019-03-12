package com.paytm.digital.education.explore.service.helper;

import static com.paytm.digital.education.explore.constants.ExploreConstants.DD_MMM_YYYY;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EVENT_TYPE_EXAM;
import static com.paytm.digital.education.explore.constants.ExploreConstants.MMM_YYYY;
import static com.paytm.digital.education.explore.constants.ExploreConstants.NON_TENTATIVE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.YYYY_MM;
import static com.paytm.digital.education.utility.DateUtil.dateToString;
import static com.paytm.digital.education.utility.DateUtil.formatDateString;
import static com.paytm.digital.education.utility.DateUtil.stringToDate;
import com.paytm.digital.education.explore.database.entity.Event;
import com.paytm.digital.education.explore.database.entity.Exam;
import com.paytm.digital.education.explore.database.entity.Instance;
import com.paytm.digital.education.explore.database.entity.SubExam;
import com.paytm.digital.education.explore.response.dto.detail.CutOff;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ExamInstanceHelper {

    private static Date MAX_DATE = new Date(Long.MAX_VALUE);

    public List<CutOff> getExamCutOffs(List<Exam> examList) {
        if (!CollectionUtils.isEmpty(examList)) {
            List<CutOff> cutOffList = new ArrayList<>();
            for (Exam exam : examList) {
                CutOff cutOff = new CutOff();
                cutOff.setExamId(exam.getExamId());
                cutOff.setExamShortName(exam.getExamShortName());
                Event event = getExamDateEvent(exam);
                if (event != null) {
                    if (NON_TENTATIVE.equalsIgnoreCase(event.getCertainty())) {
                        if (event.getDate() != null) {
                            cutOff.setExamStartDate(DD_MMM_YYYY.format(event.getDate()));
                        } else {
                            cutOff.setExamStartDate(
                                    dateToString(event.getDateRangeStart(), DD_MMM_YYYY));
                            cutOff.setExamEndDate(
                                    dateToString(event.getDateRangeEnd(), DD_MMM_YYYY));
                        }
                    } else {
                        cutOff.setExamStartDate(
                                formatDateString(event.getMonthDate(), YYYY_MM, MMM_YYYY));
                    }
                }
                cutOffList.add(cutOff);
            }
            return cutOffList;
        }
        return null;
    }

    private Event getExamDateEvent(Exam exam) {
        if (exam != null) {
            if (!CollectionUtils.isEmpty(exam.getInstances())) {
                int latestIndex = getRelevantInstanceIndex(exam.getInstances(), EVENT_TYPE_EXAM);

                if (!CollectionUtils.isEmpty(exam.getSubExams())) {
                    int parentInstanceId = exam.getInstances().get(latestIndex).getInstanceId();
                    for (SubExam subExam : exam.getSubExams()) {
                        if (!CollectionUtils.isEmpty(subExam.getInstances())) {
                            for (Instance subExamInstance : subExam.getInstances()) {
                                if (subExamInstance.getInstanceId() != null
                                        && subExamInstance.getInstanceId() == parentInstanceId) {
                                    return getExamEvent(subExamInstance.getEvents());
                                }
                            }
                        }
                    }
                }
                return getExamEvent(exam.getInstances().get(latestIndex).getEvents());
            }
        }
        return null;
    }

    private Event getExamEvent(List<Event> events) {
        if (!CollectionUtils.isEmpty(events)) {
            for (Event event : events) {
                if (EVENT_TYPE_EXAM.equalsIgnoreCase(event.getType())) {
                    return event;
                }
            }
        }
        return null;
    }

    public int getRelevantInstanceIndex(List<Instance> instances, String eventType) {
        int instanceIndex = 0;
        if (!CollectionUtils.isEmpty(instances) || instances.size() > 1) {
            Date presentDate = new Date();
            Date futureMinDate = MAX_DATE;
            Date pastMaxDate = MAX_DATE;
            for (int index = 0; index < instances.size(); index++) {
                Date minApplicationDate = MAX_DATE;
                if (!CollectionUtils.isEmpty(instances.get(index).getEvents())) {
                    List<Event> events =
                            instances.get(index).getEvents();

                    for (Event event : events) {
                        Date eventDate = null;
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

}
