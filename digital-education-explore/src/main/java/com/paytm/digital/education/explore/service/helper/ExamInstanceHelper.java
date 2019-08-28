package com.paytm.digital.education.explore.service.helper;

import static com.paytm.digital.education.constant.ExploreConstants.EXAM_DEGREES;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_CUTOFF_GENDER;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_CUTOFF_CASTEGROUP;
import static com.paytm.digital.education.constant.ExploreConstants.OTHER_CATEGORIES;
import static com.paytm.digital.education.constant.ExploreConstants.EVENT_TYPE_EXAM;
import static com.paytm.digital.education.constant.ExploreConstants.YYYY_MM;
import static com.paytm.digital.education.constant.ExploreConstants.APPLICATION;
import static com.paytm.digital.education.constant.ExploreConstants.MMM_YYYY;
import static com.paytm.digital.education.constant.ExploreConstants.NON_TENTATIVE;
import static com.paytm.digital.education.constant.ExploreConstants.EXPLORE_COMPONENT;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_SEARCH_NAMESPACE;
import static com.paytm.digital.education.constant.ExploreConstants.DATES;
import static com.paytm.digital.education.enums.Gender.OTHERS;
import static com.paytm.digital.education.utility.DateUtil.stringToDate;

import com.paytm.digital.education.database.entity.Event;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.Instance;
import com.paytm.digital.education.database.entity.SubExam;
import com.paytm.digital.education.enums.Gender;
import com.paytm.digital.education.explore.response.dto.detail.ExamAndCutOff;
import com.paytm.digital.education.utility.CommonUtil;
import com.paytm.digital.education.property.reader.PropertyReader;
import com.paytm.digital.education.utility.DateUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
@Service
public class ExamInstanceHelper {

    private PropertyReader propertyReader;

    private static Date MAX_DATE = new Date(Long.MAX_VALUE);

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

    public List<com.paytm.digital.education.explore.response.dto.detail.Event> convertEntityEventToResponse(
            String examName,
            List<Event> entityEvents) {
        List<com.paytm.digital.education.explore.response.dto.detail.Event> responseEvents =
                new ArrayList<>();

        if (!CollectionUtils.isEmpty(entityEvents)) {
            Map<String, Object> typeDisplayNames =
                    propertyReader.getPropertiesAsMapByKey(EXPLORE_COMPONENT, EXAM_SEARCH_NAMESPACE,
                            DATES);
            entityEvents.forEach(event -> {
                com.paytm.digital.education.explore.response.dto.detail.Event
                        respEvent =
                        new com.paytm.digital.education.explore.response.dto.detail.Event();
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

    private void getdatesFromSubExams(int parentInstanceId, List<SubExam> subExams,
            List<com.paytm.digital.education.explore.response.dto.detail.Event> importantDates) {
        subExams.forEach(subExam -> {
            subExam.getInstances().forEach(subExamInstance -> {
                if (subExamInstance.getParentInstanceId() == parentInstanceId) {
                    importantDates.addAll(convertEntityEventToResponse(subExam.getSubExamName(),
                            subExamInstance.getEvents()));
                }
            });
        });
    }

    public List<com.paytm.digital.education.explore.response.dto.detail.Event> getImportantDates(
            Exam exam) {
        List<com.paytm.digital.education.explore.response.dto.detail.Event> importantDates =
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
