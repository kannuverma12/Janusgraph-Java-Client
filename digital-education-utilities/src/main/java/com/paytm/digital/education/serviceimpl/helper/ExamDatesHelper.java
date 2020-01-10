package com.paytm.digital.education.serviceimpl.helper;

import com.paytm.digital.education.database.entity.Event;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.Instance;
import com.paytm.digital.education.database.entity.SubExam;
import com.paytm.digital.education.dto.detail.ImportantDate;
import com.paytm.digital.education.utility.CommonUtil;
import com.paytm.digital.education.utility.CommonUtils;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.paytm.digital.education.constant.ExploreConstants.DD_MMM_YYYY;
import static com.paytm.digital.education.constant.ExploreConstants.NON_TENTATIVE;
import static com.paytm.digital.education.constant.ExploreConstants.OTHER;
import static com.paytm.digital.education.constant.ExploreConstants.TENTATIVE_DATE;
import static com.paytm.digital.education.utility.DateUtil.dateToString;

@Component
@RequiredArgsConstructor
public class ExamDatesHelper {

    private static final Logger log =
            LoggerFactory.getLogger(ExamDatesHelper.class);

    private final ExamInstanceHelper instanceHelper;

    @Value("${exam.instances.for.date.landing.page:1}")
    private Integer noOfInstances;

    private static Comparator<Event> eventComparator = new Comparator<Event>() {
        @Override public int compare(Event o1, Event o2) {
            if (CommonUtils.isDateEqual(o1.calculateCorrespondingDate(),
                    o2.calculateCorrespondingDate())) {
                if (NON_TENTATIVE.equalsIgnoreCase(o1.getCertainty()) && NON_TENTATIVE
                        .equalsIgnoreCase(o2.getCertainty())) {
                    return 0;
                }
                return NON_TENTATIVE.equalsIgnoreCase(o1.getCertainty()) ? -1 : 1;
            }
            return o1.calculateCorrespondingDate().compareTo(o2.calculateCorrespondingDate());
        }
    };

    public List<ImportantDate> getImportantDates(Exam exam, int maxInstances) {
        List<Instance> instances = getNearestInstances(exam, maxInstances);
        return getImportantDates(instances);
    }

    private List<ImportantDate> getImportantDates(List<Instance> instanceList) {
        List<ImportantDate> importantDates = new ArrayList<>();
        boolean upcomingFound = false;
        if (!CollectionUtils.isEmpty(instanceList)) {
            Date currentDate = LocalDate.now().toDate();
            for (Instance instance : instanceList) {
                ImportantDate importantDate = new ImportantDate();
                importantDate.setPastDates(new ArrayList<>());
                importantDate.setUpcomingDates(new ArrayList<>());
                importantDate.setName(instance.getInstanceName());
                importantDate.setExamName(instance.getExamName());
                if (!CollectionUtils.isEmpty(instance.getEvents())) {
                    Collections.sort(instance.getEvents(), eventComparator);
                    for (Event event : instance.getEvents()) {
                        if (CommonUtils
                                .isDateEqualsOrAfter(event.calculateCorrespondingDate(), currentDate)) {
                            com.paytm.digital.education.dto.detail.Event respEvent = instanceHelper
                                    .convertToResponseEvent(event, instance.getExamName());
                            importantDate.getUpcomingDates().add(respEvent);
                            if (!upcomingFound) {
                                upcomingFound = updateUpcomingEventIfFound(respEvent, currentDate);
                            }
                        } else {
                            importantDate.getPastDates().add(instanceHelper
                                    .convertToResponseEvent(event, instance.getExamName()));
                        }
                    }
                    Collections.sort(importantDate.getPastDates(), Comparator.comparing(
                            com.paytm.digital.education.dto.detail.Event::calculateCorrespondingDate)
                            .reversed());
                    importantDates.add(importantDate);
                }
            }
        }
        return importantDates;
    }

    private boolean updateUpcomingEventIfFound(
            com.paytm.digital.education.dto.detail.Event respEvent, Date curDate) {
        if (Objects.isNull(respEvent.getOngoing()) || (Objects.nonNull(respEvent.getOngoing())
                && !respEvent.getOngoing())) {
            if (NON_TENTATIVE.equalsIgnoreCase(respEvent.getCertainity()) && CommonUtils
                    .isDateAfter(respEvent.getDateStartRange(), curDate)) {
                respEvent.setUpcoming(true);
                return true;
            }
        }
        return false;
    }

    public List<Instance> getNearestInstances(Exam exam, int maxInstances) {
        List<Instance> nearestInstances = nearestInstances(exam.getInstances(), maxInstances);
        if (!CollectionUtils.isEmpty(nearestInstances)) {
            for (Instance instance : nearestInstances) {
                instance.setExamName(exam.getExamShortName());
            }
        }
        if (!CollectionUtils.isEmpty(exam.getSubExams())) {
            if (!CollectionUtils.isEmpty(exam.getInstances())) {
                List<Integer> instanceIds =
                        exam.getInstances().stream().map(instance -> instance.getInstanceId())
                                .collect(Collectors.toList());
                List<Instance> subexamInstances =
                        getSubexamInstancesForParentInstance(exam.getSubExams(), instanceIds);
                subexamInstances.addAll(nearestInstances);
                return nearestInstances(subexamInstances, maxInstances);
            } else {
                List<Instance> subexamInstances = getSubexamInstances(exam.getSubExams());
                return nearestInstances(subexamInstances, maxInstances);
            }
        }
        return nearestInstances;
    }

    private List<Instance> getSubexamInstances(List<SubExam> subExams) {
        return subExams.stream().filter(subExam -> !CollectionUtils.isEmpty(subExam.getInstances()))
                .flatMap(subExam -> subExam.getInstances().stream()
                        .peek(instance -> instance.setExamName(subExam.getSubExamName())))
                .collect(Collectors.toList());
    }

    private List<Instance> getSubexamInstancesForParentInstance(List<SubExam> subExams,
            List<Integer> instanceIds) {
        return subExams.stream().filter(subExam -> !CollectionUtils.isEmpty(subExam.getInstances()))
                .flatMap(subExam -> subExam.getInstances().stream()
                        .peek(instance -> instance.setExamName(subExam.getSubExamName())))
                .filter(instance -> isHierarchicalInstance(instance.getParentInstanceId(),
                        instanceIds) && !CollectionUtils.isEmpty(instance.getEvents()))
                .collect(Collectors.toList());
    }

    private boolean isHierarchicalInstance(Integer parentInstanceId, List<Integer> instanceIds) {
        if (CollectionUtils.isEmpty(instanceIds)) {
            return true;
        }
        return (Objects.nonNull(parentInstanceId) && instanceIds.contains(parentInstanceId));
    }

    private List<Instance> nearestInstances(List<Instance> instanceList, int maxInstances) {
        List<Instance> nearestInstances = new ArrayList<>();
        List<Instance> removedInstances = new ArrayList<>();
        if (!CollectionUtils.isEmpty(instanceList)) {
            instanceList =
                    instanceList.stream()
                            .filter(x -> !CollectionUtils.isEmpty(x.getEvents()))
                            .collect(Collectors.toList());
            for (int i = 0; i < maxInstances; i++) {
                Optional<Instance> optionalInstance =
                        instanceHelper.getNearestInstance(instanceList);
                if (optionalInstance.isPresent()) {
                    nearestInstances.add(optionalInstance.get());
                    instanceList.remove(optionalInstance.get());
                    removedInstances.add(optionalInstance.get());
                }
            }
            instanceList.addAll(removedInstances);
        }
        return nearestInstances;
    }

    public Map<String, String> getNearestUpcomingAndOngoingImportantDates(Exam exam) {
        try {
            List<ImportantDate> importantDates = getImportantDates(exam, noOfInstances);
            if (!CollectionUtils.isEmpty(importantDates)) {
                ImportantDate importantDate = importantDates.get(0);
                if (!CollectionUtils.isEmpty(importantDate.getUpcomingDates())) {
                    Map<String, String> eventsMap = new LinkedHashMap<>();
                    for (com.paytm.digital.education.dto.detail.Event event : importantDate
                            .getUpcomingDates()) {
                        if (isOngoingOrUpComingEvent(event)) {
                            String eventName = OTHER.equalsIgnoreCase(event.getType()) && StringUtils
                                    .isNotBlank(event.getOtherEventLabel())
                                    ? event.getOtherEventLabel()
                                    : CommonUtil.toCamelCase(event.getType());
                            eventsMap.put(eventName, getEventDate(event));
                        }
                    }
                    return eventsMap;
                }
            }
        } catch (Exception ex) {
            log.error(
                    "Error caught while calculating important dates of similar exams. ExamId : {} ",
                    ex, exam.getExamId());
        }
        return null;
    }

    public boolean isOngoingOrUpComingEvent(com.paytm.digital.education.dto.detail.Event event) {
        return (Objects.nonNull(event.getUpcoming()) && event.getUpcoming()) || (
                Objects.nonNull(event.getOngoing()) && event.getOngoing());
    }

    private String getEventDate(com.paytm.digital.education.dto.detail.Event event) {
        if (NON_TENTATIVE.equalsIgnoreCase(event.getCertainity()) && Objects
                .nonNull(event.getDateEndRange()) && Objects.nonNull(event.getDateStartRange())) {
            return dateToString(event.getDateStartRange(), DD_MMM_YYYY) + " - " + dateToString(
                    event.getDateEndRange(), DD_MMM_YYYY);
        } else if (NON_TENTATIVE.equalsIgnoreCase(event.getCertainity()) && Objects
                .nonNull(event.getDateStartRange())) {
            return dateToString(event.getDateStartRange(), DD_MMM_YYYY);
        }
        return event.getMonthDate() + TENTATIVE_DATE;
    }
}
