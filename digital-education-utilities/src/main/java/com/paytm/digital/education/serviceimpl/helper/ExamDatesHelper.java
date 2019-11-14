package com.paytm.digital.education.serviceimpl.helper;

import com.paytm.digital.education.database.entity.Event;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.Instance;
import com.paytm.digital.education.database.entity.SubExam;
import com.paytm.digital.education.dto.detail.ImportantDate;
import com.paytm.digital.education.utility.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ExamDatesHelper {

    private final ExamInstanceHelper instanceHelper;

    public List<ImportantDate> getImportantDates(Exam exam, int maxInstances) {
        List<Instance> instances = getNearestInstances(exam, maxInstances);
        return getImportantDates(instances);
    }

    private List<ImportantDate> getImportantDates(List<Instance> instanceList) {
        List<ImportantDate> importantDates = new ArrayList<>();
        if (!CollectionUtils.isEmpty(instanceList)) {
            Date currentDate = LocalDate.now().toDate();
            for (Instance instance : instanceList) {
                ImportantDate importantDate = new ImportantDate();
                importantDate.setPastDates(new ArrayList<>());
                importantDate.setUpcomingDates(new ArrayList<>());
                importantDate.setName(instance.getInstanceName());
                importantDate.setExamName(instance.getExamName());
                if (!CollectionUtils.isEmpty(instance.getEvents())) {
                    for (Event event : instance.getEvents()) {
                        if (CommonUtils
                                .isDateEqualsOrAfter(event.calculateCorrespondingDate(), currentDate)) {
                            importantDate.getUpcomingDates().add(instanceHelper
                                    .convertToResponseEvent(event, instance.getExamName()));
                        } else {
                            importantDate.getPastDates().add(instanceHelper
                                    .convertToResponseEvent(event, instance.getExamName()));
                        }
                    }
                    importantDates.add(importantDate);
                }
            }
        }
        return importantDates;
    }

    public List<Instance> getNearestInstances(Exam exam, int maxInstances) {
        List<Instance> nearestInstances = nearestInstances(exam.getInstances(), maxInstances);
        if (!CollectionUtils.isEmpty(nearestInstances)) {
            for (Instance instance : nearestInstances) {
                instance.setExamName(exam.getExamShortName());
            }
        }
        if (!CollectionUtils.isEmpty(exam.getSubExams())) {
            if (!CollectionUtils.isEmpty(nearestInstances)) {
                List<Integer> instanceIds =
                        nearestInstances.stream().map(instance -> instance.getInstanceId())
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

    private List<Instance> getSubexamInstancesForParentInstance(List<SubExam> subExams, List<Integer> instanceIds) {
        return subExams.stream().filter(subExam -> !CollectionUtils.isEmpty(subExam.getInstances()))
                .flatMap(subExam -> subExam.getInstances().stream()
                        .peek(instance -> instance.setExamName(subExam.getSubExamName())))
                .filter(instance -> isHierarchicalInstance(instance.getParentInstanceId(),
                        instanceIds)).collect(Collectors.toList());
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
}
