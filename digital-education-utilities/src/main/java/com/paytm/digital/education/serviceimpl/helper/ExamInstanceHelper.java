package com.paytm.digital.education.serviceimpl.helper;

import static com.paytm.digital.education.constant.CommonConstants.APPLICATION;
import static com.paytm.digital.education.constant.CommonConstants.DATES;
import static com.paytm.digital.education.constant.CommonConstants.EVENT_TYPE_EXAM;
import static com.paytm.digital.education.constant.CommonConstants.EXAM_CUTOFF_CASTEGROUP;
import static com.paytm.digital.education.constant.CommonConstants.EXAM_CUTOFF_GENDER;
import static com.paytm.digital.education.constant.CommonConstants.EXAM_DEGREES;
import static com.paytm.digital.education.constant.CommonConstants.EXAM_SEARCH_NAMESPACE;
import static com.paytm.digital.education.constant.CommonConstants.EXPLORE_COMPONENT;
import static com.paytm.digital.education.constant.CommonConstants.MMM_YYYY;
import static com.paytm.digital.education.constant.CommonConstants.OTHER_CATEGORIES;
import static com.paytm.digital.education.constant.CommonConstants.YYYY_MM;
import static com.paytm.digital.education.constant.CommonConstants.ZERO;
import static com.paytm.digital.education.enums.Gender.OTHERS;
import static java.util.Collections.emptyList;

import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.Instance;
import com.paytm.digital.education.database.entity.SubExam;
import com.paytm.digital.education.dto.detail.ExamAndCutOff;
import com.paytm.digital.education.dto.detail.Section;
import com.paytm.digital.education.dto.detail.Syllabus;
import com.paytm.digital.education.dto.detail.Topic;
import com.paytm.digital.education.dto.detail.Unit;
import com.paytm.digital.education.enums.Gender;
import com.paytm.digital.education.property.reader.PropertyReader;
import com.paytm.digital.education.utility.CommonUtil;
import com.paytm.digital.education.utility.CommonUtils;
import com.paytm.digital.education.utility.DateUtil;
import com.paytm.digital.education.database.entity.Event;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

@AllArgsConstructor
@Service
public class ExamInstanceHelper {

    private PropertyReader propertyReader;

    private static Date MAX_DATE = new Date(Long.MAX_VALUE);

    public List<Syllabus> getSyllabus(Instance nearestInstance,
            Map<String, Instance> subExamInstances, Exam exam) {

        List<Syllabus> syllabus = new ArrayList<>();

        if (!CollectionUtils.isEmpty(subExamInstances)) {
            for (Map.Entry<String, Instance> entry : subExamInstances.entrySet()) {
                if (!CollectionUtils.isEmpty(entry.getValue().getSyllabusList())) {
                    List<Section> sections =
                            getSectionsFromEntitySyllabus(entry.getValue().getSyllabusList());
                    syllabus.add(
                            Syllabus.builder().subExamName(entry.getKey()).sections(sections)
                                    .build());
                }
            }
        }

        if (CollectionUtils.isEmpty(syllabus) && !CollectionUtils
                .isEmpty(nearestInstance.getSyllabusList())) {
            List<Section> sections =
                    getSectionsFromEntitySyllabus(nearestInstance.getSyllabusList());
            syllabus.add(Syllabus.builder().sections(sections).subExamName(exam.getExamFullName())
                    .build());
        }

        if (CollectionUtils.isEmpty(syllabus) && !CollectionUtils.isEmpty(exam.getSyllabus())) {
            List<Section> sections = getSectionsFromEntitySyllabus(exam.getSyllabus());
            syllabus.add(Syllabus.builder().subExamName(exam.getExamFullName()).sections(sections)
                    .build());
        }
        if (!CollectionUtils.isEmpty(syllabus)) {
            return syllabus;
        }
        return null;
    }

    public List<ExamAndCutOff> getExamCutOffs(List<Exam> examList,
            Map<String, Object> examRelatedData, Set<Long> examIds) {
        Map<Long, String> examIdAndMasterDegrees =
                (Map<Long, String>) examRelatedData.get(EXAM_DEGREES);
        Map<Long, Map<Gender, String>> examGender =
                (Map<Long, Map<Gender, String>>) examRelatedData.get(EXAM_CUTOFF_GENDER);
        Map<Long, Map<String, String>> examCategoryGroup =
                (Map<Long, Map<String, String>>) examRelatedData.get(EXAM_CUTOFF_CASTEGROUP);
        if (!CollectionUtils.isEmpty(examList)) {
            List<ExamAndCutOff> cutOffList = new ArrayList<>();
            for (Exam exam : examList) {
                long examId = exam.getExamId();
                ExamAndCutOff examAndCutOff = new ExamAndCutOff();
                examAndCutOff.setExamId(examId);
                examAndCutOff.setFullName(exam.getExamFullName());
                examAndCutOff.setUrlDisplayKey(
                        CommonUtil.convertNameToUrlDisplayName(exam.getExamFullName()));
                examAndCutOff.setExamShortName(exam.getExamShortName());
                examAndCutOff.setMasterDegree(examIdAndMasterDegrees.get(examId));
                if (examCategoryGroup.containsKey(examId)) {
                    setCasteGroupAndGender(examId, examGender, examCategoryGroup, examAndCutOff);
                    examAndCutOff.setHasCutoff(true);
                } else {
                    examAndCutOff.setHasCutoff(false);
                }
                /*
                 **remove exam id to avoid traversing subexam (for loop) when there is no exam id
                 ** left
                 */
                examIds.remove(examId);
                /*
                 ** Get the data of the first subexam which is present in the
                 ** examIds set
                 */
                if (examAndCutOff.getHasCutoff() == false && !examIds.isEmpty()) {
                    if (Objects.nonNull(exam.getSubExams())) {
                        for (SubExam subExam : exam.getSubExams()) {
                            examId = subExam.getId();
                            if (examIds.contains(examId) && examCategoryGroup.containsKey(examId)) {
                                examAndCutOff.setExamId(examId);
                                setCasteGroupAndGender(examId, examGender, examCategoryGroup,
                                        examAndCutOff);
                                examAndCutOff.setHasCutoff(true);
                                // this is done so that we can do empty check correctly in the examIds
                                examIds.remove(examId);
                                break;
                            }
                        }
                    }
                }
                cutOffList.add(examAndCutOff);
            }
            return cutOffList;
        }
        return null;
    }

    public Optional<Instance> getNearestInstance(List<Instance> instances) {
        Date presentDate = new Date();

        Optional<Instance> nearestFutureInstance = getInstanceAccordingToFilterAndComparator(
            Optional.ofNullable(instances).orElse(emptyList()),
            holder -> CommonUtils.isDateEqualsOrAfter(holder.getDate(), presentDate),
            Comparator.comparing(EventInstanceDateHolder::getDate));

        if (nearestFutureInstance.isPresent()) {
            return nearestFutureInstance;
        } else {
            Optional<Instance> nearestPastInstance = getInstanceAccordingToFilterAndComparator(
                instances,
                x -> true,
                Comparator.comparing(EventInstanceDateHolder::getDate).reversed());
            return nearestPastInstance;
        }
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
                        Date eventDate = event.calculateCorrespondingDate();

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

    public List<com.paytm.digital.education.dto.detail.Event> convertEntityEventToResponse(
            String examName,
            List<Event> entityEvents) {
        List<com.paytm.digital.education.dto.detail.Event> responseEvents =
                new ArrayList<>();

        if (!CollectionUtils.isEmpty(entityEvents)) {
            Map<String, Object> typeDisplayNames =
                    propertyReader.getPropertiesAsMapByKey(EXPLORE_COMPONENT, EXAM_SEARCH_NAMESPACE,
                            DATES);
            entityEvents.forEach(event -> {
                com.paytm.digital.education.dto.detail.Event
                        respEvent =
                        new com.paytm.digital.education.dto.detail.Event();
                respEvent.setName(examName);
                if (event.getDateRangeStart() != null) {
                    respEvent.setDateEndRange(event.getDateRangeEnd());
                    respEvent.setDateStartRange(event.getDateRangeStart());
                    respEvent.setDateEndRangeTimestamp(event.getDateRangeEnd());
                    respEvent.setDateStartRangeTimestamp(event.getDateRangeStart());
                } else {
                    respEvent.setDateStartRangeTimestamp(event.getDate());
                    respEvent.setDateStartRange(event.getDate());
                }
                respEvent.setMonthTimestamp(DateUtil.stringToDate(event.getMonthDate(), YYYY_MM));
                respEvent.setMonthDate(
                        DateUtil.formatDateString(event.getMonthDate(), YYYY_MM, MMM_YYYY));
                if (!CollectionUtils.isEmpty(typeDisplayNames) && typeDisplayNames
                        .containsKey(event.getType())) {
                    respEvent.setTypeDisplayName((String) typeDisplayNames.get(event.getType()));
                } else {
                    respEvent.setTypeDisplayName(event.getType());
                }
                respEvent.setModes(event.getModes());
                respEvent.setType(event.getType());
                respEvent.setCertainity(event.getCertainty());
                responseEvents.add(respEvent);
            });
        }
        return responseEvents;
    }

    public Map<String, Instance> getSubExamInstances(Exam exam, int parentInstanceId) {
        Map<String, Instance> subExamInstances = new HashMap<>();
        if (!CollectionUtils.isEmpty(exam.getSubExams())) {
            for (SubExam subExam : exam.getSubExams()) {
                if (!CollectionUtils.isEmpty(subExam.getInstances())) {
                    for (Instance instance : subExam
                            .getInstances()) {
                        if (instance.getParentInstanceId() == parentInstanceId) {
                            subExamInstances.put(subExam.getSubExamName(), instance);
                        }
                    }
                }
            }
        }
        return subExamInstances;
    }

    public List<com.paytm.digital.education.dto.detail.Event> getImportantDates(
            Exam exam, Instance nearestInstance, Map<String, Instance> subExamInstances) {
        List<com.paytm.digital.education.dto.detail.Event> importantDates =
                new ArrayList<>();
        List<Event> events = nearestInstance.getEvents();
        importantDates.addAll(convertEntityEventToResponse(exam.getExamFullName(), events));
        for (Map.Entry<String, Instance> entry : subExamInstances.entrySet()) {
            importantDates.addAll(convertEntityEventToResponse(entry.getKey(),
                    entry.getValue().getEvents()));
        }
        if (!CollectionUtils.isEmpty(importantDates)) {
            return importantDates;
        }
        return null;
    }

    private List<Topic> getTopics(
            com.paytm.digital.education.database.entity.Unit entityUnit) {
        List<Topic> topics = new ArrayList<>();
        if (!entityUnit.getName().equals(ZERO) && !CollectionUtils
                .isEmpty(entityUnit.getTopics())) {
            entityUnit.getTopics().forEach(entityTopic -> {
                if (!entityTopic.getName().equals(ZERO)) {
                    topics.add(new Topic(entityTopic.getName()));
                }
            });
        }
        return topics;
    }

    private List<Unit> getUnits(
            com.paytm.digital.education.database.entity.Syllabus entitySyllabus) {
        List<Unit> units = new ArrayList<>();
        if (!CollectionUtils.isEmpty(entitySyllabus.getUnits())) {
            entitySyllabus.getUnits().forEach(entityUnit -> {
                List<Topic> topics = getTopics(entityUnit);
                if (!CollectionUtils.isEmpty(topics)) {
                    units.add(new Unit(entityUnit.getName(), topics));
                }
            });
        }
        return units;
    }

    private List<Section> getSectionsFromEntitySyllabus(
            List<com.paytm.digital.education.database.entity.Syllabus> entitySyllabusList) {
        List<Section> sectionList = new ArrayList<>();
        entitySyllabusList.forEach(entitySection -> {
            List<Unit> units = getUnits(entitySection);
            if (!CollectionUtils.isEmpty(units)) {
                Section section = new Section(entitySection.getSubjectName(), units);
                sectionList.add(section);
            }
        });
        return sectionList;
    }

    private void setCasteGroupAndGender(Long examId, Map<Long, Map<Gender, String>> examGender,
            Map<Long, Map<String, String>> examCategoryGroup, ExamAndCutOff examAndCutOff) {
        Map<String, String> casteGroups = examCategoryGroup.get(examId);
        Map<Gender, String> genders = examGender.get(examId);
        if (!casteGroups.isEmpty() && !(casteGroups.size() == 1 && casteGroups.entrySet().iterator()
                .next().getKey()
                .equals(OTHER_CATEGORIES))) {
            examAndCutOff.setCasteGroups(casteGroups);
        }
        if (!genders.isEmpty() && !(genders.size() == 1 && genders.entrySet().iterator().next()
                .getKey()
                .equals(OTHERS))) {
            examAndCutOff.setGenders(genders);
        }
    }
    
    private Optional<Instance> getInstanceAccordingToFilterAndComparator(
            List<Instance> instances,
            Predicate<EventInstanceDateHolder> predicate,
            Comparator<EventInstanceDateHolder> comparator) {
        return instances.stream()
                .flatMap(instance ->
                        Optional.ofNullable(instance.getEvents())
                                .orElse(emptyList())
                                .stream()
                                .map(event ->
                                        new EventInstanceDateHolder(event, instance,
                                                event.calculateCorrespondingDate())))
                .filter(predicate)
                .min(comparator)
                .map(EventInstanceDateHolder::getInstance);
    }

    private void getdatesFromSubExams(int parentInstanceId, List<SubExam> subExams,
            List<com.paytm.digital.education.dto.detail.Event> importantDates) {
        subExams.forEach(subExam -> {
            subExam.getInstances().forEach(subExamInstance -> {
                if (subExamInstance.getParentInstanceId() == parentInstanceId) {
                    importantDates.addAll(convertEntityEventToResponse(subExam.getSubExamName(),
                            subExamInstance.getEvents()));
                }
            });
        });
    }

    public List<com.paytm.digital.education.dto.detail.Event> getImportantDates(
            Exam exam) {
        List<com.paytm.digital.education.dto.detail.Event> importantDates =
                new ArrayList<>();
        int instanceIndex = -1;
        if (!CollectionUtils.isEmpty(exam.getInstances())) {
            instanceIndex = getRelevantInstanceIndex(exam.getInstances(), APPLICATION);
            importantDates.addAll(convertEntityEventToResponse(exam.getExamFullName(),
                    exam.getInstances().get(instanceIndex).getEvents()));
        }
        if (!CollectionUtils.isEmpty(exam.getSubExams()) && instanceIndex != -1) {
            getdatesFromSubExams(exam.getInstances().get(instanceIndex).getInstanceId(),
                    exam.getSubExams(), importantDates);
        }
        return importantDates;
    }
}
