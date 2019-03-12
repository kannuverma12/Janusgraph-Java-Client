package com.paytm.digital.education.explore.service.impl;

import static com.paytm.digital.education.explore.constants.ExploreConstants.APPLICATION;
import static com.paytm.digital.education.explore.constants.ExploreConstants.DD_MMM_YYYY;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_PREFIX;
import static com.paytm.digital.education.explore.constants.ExploreConstants.MMM_YYYY;
import static com.paytm.digital.education.explore.constants.ExploreConstants.NON_TENTATIVE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.YYYY_MM;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_EXAM_ID;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.explore.database.entity.Exam;
import com.paytm.digital.education.explore.database.entity.Instance;
import com.paytm.digital.education.explore.database.entity.SubExam;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.response.dto.detail.Event;
import com.paytm.digital.education.explore.response.dto.detail.ExamDetail;
import com.paytm.digital.education.explore.response.dto.detail.Section;
import com.paytm.digital.education.explore.response.dto.detail.Syllabus;
import com.paytm.digital.education.explore.response.dto.detail.Topic;
import com.paytm.digital.education.explore.response.dto.detail.Unit;
import com.paytm.digital.education.explore.service.helper.ExamInstanceHelper;
import com.paytm.digital.education.mapping.ErrorEnum;
import com.paytm.digital.education.utility.DateUtil;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ExamDetailServiceImpl {

    private CommonMongoRepository commonMongoRepository;

    private static int            EXAM_PREFIX_LENGTH = EXAM_PREFIX.length();

    public ExamDetail getDetail(Long entityId, Long userId,
            String fieldGroup, List<String> fields) throws ParseException {

        // TODO: fields are not being supported currently. Part of discussion
        List<String> groupFields =
                commonMongoRepository.getFieldsByGroup(Exam.class, fieldGroup);
        List<String> examFields = new ArrayList<>();
        if (!CollectionUtils.isEmpty(groupFields)) {
            for (String requestedField : groupFields) {
                if (requestedField.contains(EXAM_PREFIX)) {
                    examFields.add(requestedField
                            .substring(EXAM_PREFIX_LENGTH, requestedField.length()));
                }
            }
        }

        Exam exam =
                commonMongoRepository.getEntityByFields(EXAM_ID, entityId, Exam.class,
                        examFields);

        if (exam != null) {
            return processExamDetail(exam, examFields, userId);
        }
        throw new BadRequestException(ErrorEnum.INVALID_EXAM_ID,
                INVALID_EXAM_ID.getExternalMessage());
    }

    private int getRelevantInstanceIndex(List<Instance> instances) throws ParseException {
        int instanceIndex = 0;
        if (!CollectionUtils.isEmpty(instances) || instances.size() > 1) {
            Date presentDate = new Date();
            Date futureMinDate = new Date(Long.MAX_VALUE);
            Date pastMaxDate = new Date(Long.MIN_VALUE);
            for (int index = 0; index < instances.size(); index++) {
                Date minApplicationDate = new Date(Long.MAX_VALUE);
                if (!CollectionUtils.isEmpty(instances.get(index).getEvents())) {
                    List<com.paytm.digital.education.explore.database.entity.Event> events =
                            instances.get(index).getEvents();
                    for (int eventIndex = 0; eventIndex < events.size(); eventIndex++) {
                        if (events.get(eventIndex).getType() != null
                                && events.get(eventIndex).getType().equalsIgnoreCase(APPLICATION)) {
                            Date eventDate;
                            if (events.get(eventIndex).getCertainty() != null
                                    && events.get(eventIndex).getCertainty()
                                            .equalsIgnoreCase(NON_TENTATIVE)) {
                                eventDate = events.get(eventIndex).getDateRangeStart() != null
                                        ? events.get(eventIndex).getDateRangeStart()
                                        : events.get(eventIndex).getDate();
                            } else {
                                eventDate = DateUtil.stringToDate(
                                        events.get(eventIndex).getMonthDate(), DD_MMM_YYYY);
                            }
                            if (eventDate != null && minApplicationDate.compareTo(eventDate) > 0) {
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
        }
        return instanceIndex;
    }

    private ExamDetail processExamDetail(Exam exam, List<String> examFields, Long userId)
            throws ParseException {
        ExamDetail examDetail = buildResponse(exam);;
        return examDetail;
    }

    private List<Section> getSectionsFromEntitySyllabus(
            List<com.paytm.digital.education.explore.database.entity.Syllabus> entitySyllabusList) {
        List<Section> sectionList = new ArrayList<>();
        entitySyllabusList.forEach(entitySection -> {
            List<Unit> units = new ArrayList<>();
            entitySection.getUnits().forEach(entityUnit -> {
                List<Topic> topics = new ArrayList<>();
                entityUnit.getTopics().forEach(entityTopic -> {
                    Topic topic = new Topic(entityTopic.getName());
                    topics.add(topic);
                });
                Unit unit = new Unit(entityUnit.getName(), topics);
                units.add(unit);
            });
            Section section = new Section(entitySection.getSubjectName(), units);
            sectionList.add(section);
        });
        return sectionList;
    }

    private List<Event> convertEntityEventToResponse(String examName,
            List<com.paytm.digital.education.explore.database.entity.Event> entityEvents) {
        List<Event> responseEvents = new ArrayList<>();

        if (!CollectionUtils.isEmpty(entityEvents)) {
            entityEvents.forEach(event -> {
                Event respEvent = new Event();
                respEvent.setName(examName);
                respEvent.setDate(event.getDate());
                respEvent.setDateEndRange(event.getDateRangeEnd());
                respEvent.setDateStartRange(event.getDateRangeStart());
                respEvent.setMonthDate(
                        DateUtil.formatDateString(event.getMonthDate(), YYYY_MM, MMM_YYYY));
                respEvent.setModes(event.getModes());
                respEvent.setType(event.getType());
                respEvent.setCertainity(event.getCertainty());
                responseEvents.add(respEvent);
            });
        }
        return responseEvents;
    }

    private void addDatesToResponse(ExamDetail examDetail, List<Event> importantDates) {
        for (int i = 0; i < importantDates.size(); i++) {
            if (importantDates.get(i).getType().equalsIgnoreCase(APPLICATION)) {
                if (importantDates.get(i).getCertainity() != null
                        && importantDates.get(i).getCertainity().equalsIgnoreCase(NON_TENTATIVE)) {
                    if (importantDates.get(i).getDateStartRange() != null) {
                        examDetail.setApplicationOpening(
                                DateUtil.dateToString(importantDates.get(i).getDateStartRange(),
                                        DD_MMM_YYYY));
                        examDetail.setApplicationClosing(DateUtil.dateToString(
                                importantDates.get(i).getDateEndRange(), DD_MMM_YYYY));
                    } else {
                        examDetail.setApplicationOpening(DateUtil
                                .dateToString(importantDates.get(i).getDate(), DD_MMM_YYYY));
                    }

                } else {
                    examDetail.setApplicationMonth(DateUtil.formatDateString(
                            importantDates.get(i).getMonthDate(), YYYY_MM, DD_MMM_YYYY));
                }
            } else if (importantDates.get(i).getType().equalsIgnoreCase(EXAM)) {
                if (importantDates.get(i).getCertainity() != null
                        && importantDates.get(i).getCertainity().equalsIgnoreCase(NON_TENTATIVE)) {
                    if (importantDates.get(i).getDateStartRange() != null) {
                        examDetail.setExamStartDate(
                                DateUtil.dateToString(importantDates.get(i).getDateStartRange(),
                                        DD_MMM_YYYY));
                        examDetail.setExamEndDate(
                                DateUtil.dateToString(importantDates.get(i).getDateEndRange(),
                                        DD_MMM_YYYY));
                    } else {
                        examDetail.setExamStartDate(
                                DateUtil.dateToString(importantDates.get(i).getDate(),
                                        DD_MMM_YYYY));
                    }

                } else {
                    examDetail
                            .setExamMonth(DateUtil.formatDateString(
                                    importantDates.get(i).getMonthDate(), YYYY_MM, DD_MMM_YYYY));
                }
            }
        }
    }

    private void addSubExamData(int parentInstanceId, List<SubExam> subExams,
            ExamDetail examDetail, List<Event> importantDates) {
        List<Syllabus> syllabusList = new ArrayList<>();
        subExams.forEach(subExam -> {
            subExam.getInstances().forEach(subExamInstance -> {
                if (subExamInstance.getParentInstanceId() == parentInstanceId) {
                    Syllabus syllabus = new Syllabus(subExam.getSubExamName(),
                            getSectionsFromEntitySyllabus(subExamInstance.getSyllabusList()));
                    syllabusList.add(syllabus);
                    importantDates
                            .addAll(convertEntityEventToResponse(subExam.getSubExamName(),
                                    subExamInstance.getEvents()));
                }
            });
        });
        if (syllabusList.size() != 0) {
            examDetail.setSyllabus(syllabusList);
        }
    }

    private ExamDetail buildResponse(Exam exam) throws ParseException {
        ExamDetail examDetail = new ExamDetail();
        examDetail.setExamId(exam.getExamId());
        examDetail.setAbout(exam.getAboutExam());
        examDetail.setExamId(exam.getExamId());
        examDetail.setExamFullName(exam.getExamFullName());
        examDetail.setExamShortName(exam.getExamShortName());
        examDetail.setLinguisticMedium(exam.getLinguisticMediumExam());
        examDetail.setExamLevel(exam.getLevelOfExam());
        examDetail.setDocumentsRequiredAtExam(exam.getDocumentsExam());
        examDetail.setDocumentsRequiredAtCounselling(exam.getDocumentsCounselling());
        examDetail.setAdmitCard("");
        examDetail.setAnswerKey("");
        examDetail.setApplicationProcess("");
        examDetail.setCounselling("");
        examDetail.setResult("");
        List<Event> importantDates = new ArrayList<>();
        int instanceIndex = -1;
        if (!CollectionUtils.isEmpty(exam.getInstances())) {
            instanceIndex = getRelevantInstanceIndex(exam.getInstances());
            int centersCount = exam.getInstances().get(instanceIndex).getExamCenters().size();
            examDetail.setCentersCount(centersCount);
            importantDates.addAll(convertEntityEventToResponse(exam.getExamFullName(),
                    exam.getInstances().get(instanceIndex).getEvents()));
        }
        if (!CollectionUtils.isEmpty(exam.getSubExams()) && instanceIndex != -1) {
            int parentInstanceId = exam.getInstances().get(instanceIndex).getInstanceId();
            examDetail.setDurationInHour(exam.getSubExams().get(0).getDurationHours());
            addSubExamData(parentInstanceId, exam.getSubExams(), examDetail, importantDates);
        }
        examDetail.setImportantDates(importantDates);
        if (CollectionUtils.isEmpty(examDetail.getSyllabus())) {
            List<Syllabus> syllabusList = new ArrayList<>();
            if (!CollectionUtils
                    .isEmpty(exam.getInstances().get(instanceIndex).getSyllabusList())) {
                List<Section> sections =
                        getSectionsFromEntitySyllabus(
                                exam.getInstances().get(instanceIndex).getSyllabusList());
                syllabusList.add(new Syllabus(exam.getExamFullName(), sections));
            } else if (!CollectionUtils.isEmpty(exam.getSyllabus())) {
                List<Section> sections = getSectionsFromEntitySyllabus(exam.getSyllabus());
                syllabusList.add(new Syllabus(exam.getExamFullName(), sections));
            }
            examDetail.setSyllabus(syllabusList);
        }
        if (examDetail.getDurationInHour() == null) {
            examDetail.setDurationInHour(exam.getExamDuration());
        }
        addDatesToResponse(examDetail, importantDates);
        return examDetail;
    }

}
