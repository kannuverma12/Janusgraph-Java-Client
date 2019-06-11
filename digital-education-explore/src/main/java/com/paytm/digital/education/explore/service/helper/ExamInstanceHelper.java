package com.paytm.digital.education.explore.service.helper;

import static com.paytm.digital.education.explore.constants.ExploreConstants.EVENT_TYPE_EXAM;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_CUTOFF_CASTEGROUP;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_CUTOFF_GENDER;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_DEGREES;
import static com.paytm.digital.education.explore.constants.ExploreConstants.NON_TENTATIVE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.OTHER_CATEGORIES;
import static com.paytm.digital.education.explore.constants.ExploreConstants.YYYY_MM;
import static com.paytm.digital.education.explore.enums.Gender.OTHERS;
import static com.paytm.digital.education.utility.DateUtil.stringToDate;

import com.paytm.digital.education.explore.database.entity.Event;
import com.paytm.digital.education.explore.database.entity.Exam;
import com.paytm.digital.education.explore.database.entity.Instance;
import com.paytm.digital.education.explore.database.entity.SubExam;
import com.paytm.digital.education.explore.enums.Gender;
import com.paytm.digital.education.explore.response.dto.detail.ExamAndCutOff;
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
                            if (examIds.contains(examId)) {
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
        if (Objects.nonNull(casteGroups) && !casteGroups.isEmpty() && !(casteGroups.size() == 1
                && casteGroups.entrySet().iterator()
                .next().getKey()
                .equals(OTHER_CATEGORIES))) {
            examAndCutOff.setCasteGroups(casteGroups);
        }
        if (Objects.nonNull(genders) && !genders.isEmpty() && !(genders.size() == 1 && genders
                .entrySet().iterator().next()
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
}
