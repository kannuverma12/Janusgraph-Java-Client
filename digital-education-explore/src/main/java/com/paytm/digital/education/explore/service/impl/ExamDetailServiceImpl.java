package com.paytm.digital.education.explore.service.impl;

import static com.paytm.digital.education.explore.constants.ExploreConstants.APPLICATION;
import static com.paytm.digital.education.explore.constants.ExploreConstants.DD_MMM_YYYY;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_FILTER_NAMESPACE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_PREFIX;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXPLORE_COMPONENT;
import static com.paytm.digital.education.explore.constants.ExploreConstants.LINGUISTIC_MEDIUM;
import static com.paytm.digital.education.explore.constants.ExploreConstants.LINGUISTIC_MEDIUM_NAMESPACE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.MMM_YYYY;
import static com.paytm.digital.education.explore.constants.ExploreConstants.NON_TENTATIVE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.YYYY_MM;
import static com.paytm.digital.education.explore.enums.EducationEntity.EXAM;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_EXAM_ID;

import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.explore.database.entity.Exam;
import com.paytm.digital.education.explore.database.entity.Instance;
import com.paytm.digital.education.explore.database.entity.SubExam;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.response.dto.detail.Event;
import com.paytm.digital.education.explore.response.dto.detail.ExamDetail;
import com.paytm.digital.education.explore.response.dto.detail.Location;
import com.paytm.digital.education.explore.response.dto.detail.Section;
import com.paytm.digital.education.explore.response.dto.detail.Syllabus;
import com.paytm.digital.education.explore.response.dto.detail.Topic;
import com.paytm.digital.education.explore.response.dto.detail.Unit;
import com.paytm.digital.education.explore.service.helper.BannerDataHelper;
import com.paytm.digital.education.explore.service.helper.DerivedAttributesHelper;
import com.paytm.digital.education.explore.service.helper.DetailPageSectionHelper;
import com.paytm.digital.education.explore.service.helper.ExamInstanceHelper;
import com.paytm.digital.education.explore.service.helper.WidgetsDataHelper;
import com.paytm.digital.education.explore.utility.CommonUtil;
import com.paytm.digital.education.mapping.ErrorEnum;
import com.paytm.digital.education.property.reader.PropertyReader;
import com.paytm.digital.education.utility.DateUtil;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Service
public class ExamDetailServiceImpl {

    private CommonMongoRepository   commonMongoRepository;
    private ExamInstanceHelper      examInstanceHelper;
    private PropertyReader          propertyReader;
    private DerivedAttributesHelper derivedAttributesHelper;
    private DetailPageSectionHelper detailPageSectionHelper;
    private BannerDataHelper        bannerDataHelper;
    private WidgetsDataHelper       widgetsDataHelper;

    private static int EXAM_PREFIX_LENGTH = EXAM_PREFIX.length();

    //TODO - modularize methods for caching as. Its fine as of now as userId is not being used As of now.
    @Cacheable(value = "exam_detail")
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

    private ExamDetail processExamDetail(Exam exam, List<String> examFields, Long userId)
            throws ParseException {
        ExamDetail examDetail = buildResponse(exam);
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
                if (event.getDateRangeStart() != null) {
                    respEvent.setDateEndRange(event.getDateRangeEnd());
                    respEvent.setDateStartRange(event.getDateRangeStart());
                } else {
                    respEvent.setDateStartRange(event.getDate());
                }
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
                    if (importantDates.get(i).getDateEndRange() != null) {
                        examDetail.setApplicationOpening(
                                DateUtil.dateToString(importantDates.get(i).getDateStartRange(),
                                        DD_MMM_YYYY));
                        examDetail.setApplicationClosing(DateUtil.dateToString(
                                importantDates.get(i).getDateEndRange(), DD_MMM_YYYY));
                    } else {
                        examDetail.setApplicationOpening(DateUtil
                                .dateToString(importantDates.get(i).getDateStartRange(),
                                        DD_MMM_YYYY));
                    }

                } else {
                    examDetail.setApplicationMonth(DateUtil.formatDateString(
                            importantDates.get(i).getMonthDate(), YYYY_MM, MMM_YYYY));
                }
            } else if (importantDates.get(i).getType().equalsIgnoreCase(EXAM.name())) {
                if (importantDates.get(i).getCertainity() != null
                        && importantDates.get(i).getCertainity().equalsIgnoreCase(NON_TENTATIVE)) {
                    if (importantDates.get(i).getDateEndRange() != null) {
                        examDetail.setExamStartDate(
                                DateUtil.dateToString(importantDates.get(i).getDateStartRange(),
                                        DD_MMM_YYYY));
                        examDetail.setExamEndDate(
                                DateUtil.dateToString(importantDates.get(i).getDateEndRange(),
                                        DD_MMM_YYYY));
                    } else {
                        examDetail.setExamStartDate(
                                DateUtil.dateToString(importantDates.get(i).getDateStartRange(),
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
                    if (!CollectionUtils.isEmpty(subExamInstance.getSyllabusList())) {
                        Syllabus syllabus = new Syllabus(subExam.getSubExamName(),
                                getSectionsFromEntitySyllabus(subExamInstance.getSyllabusList()));
                        syllabusList.add(syllabus);
                    }
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

    private void setLanguageFromLanguageCodes(ExamDetail examDetail,
            List<String> linguisticMediumCodes) {
        Map<String, Object> propertyMap = propertyReader
                .getPropertiesAsMapByKey(EXPLORE_COMPONENT, EXAM_FILTER_NAMESPACE,
                        LINGUISTIC_MEDIUM_NAMESPACE);
        List<String> examLang = new ArrayList<>();
        linguisticMediumCodes.forEach(code -> {
            examLang.add(CommonUtil.getDisplayName(propertyMap, code));
        });
        examDetail.setLinguisticMedium(examLang);
    }

    private ExamDetail buildResponse(Exam exam) throws ParseException {
        ExamDetail examDetail = new ExamDetail();
        examDetail.setExamId(exam.getExamId());
        examDetail.setAbout(exam.getAboutExam());
        examDetail.setExamId(exam.getExamId());
        examDetail.setExamFullName(exam.getExamFullName());
        examDetail.setExamShortName(exam.getExamShortName());
        if (!CollectionUtils.isEmpty(exam.getLinguisticMediumExam())) {
            setLanguageFromLanguageCodes(examDetail, exam.getLinguisticMediumExam());
        }
        examDetail.setExamLevel(exam.getLevelOfExam());
        examDetail.setDocumentsRequiredAtExam(exam.getDocumentsExam());
        examDetail.setDocumentsRequiredAtCounselling(exam.getDocumentsCounselling());
        examDetail.setAdmitCard("");
        examDetail.setAnswerKey("");
        examDetail.setApplicationProcess("");
        examDetail.setCounselling("");
        examDetail.setResult("");
        examDetail.setExamCenters(getExamCenters(exam.getInstances()));
        List<Event> importantDates = new ArrayList<>();
        int instanceIndex = -1;
        if (!CollectionUtils.isEmpty(exam.getInstances())) {
            instanceIndex =
                    examInstanceHelper.getRelevantInstanceIndex(exam.getInstances(), APPLICATION);
            if (!CollectionUtils.isEmpty(exam.getInstances().get(instanceIndex).getExamCenters())) {
                int centersCount = exam.getInstances().get(instanceIndex).getExamCenters().size();
                examDetail.setCentersCount(centersCount);
            }
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
        String entityName = EXAM.name().toLowerCase();
        Map<String, Object> highlights = new HashMap<>();
        highlights.put(entityName, exam);
        highlights.put(LINGUISTIC_MEDIUM, examDetail.getLinguisticMedium());
        examDetail.setDerivedAttributes(
                derivedAttributesHelper.getDerivedAttributes(highlights,
                        entityName));
        addDatesToResponse(examDetail, importantDates);
        examDetail.setSections(detailPageSectionHelper.getSectionOrder(entityName));
        examDetail.setBanners(bannerDataHelper.getBannerData(entityName));
        examDetail.setWidgets(widgetsDataHelper.getWidgets(entityName, exam.getExamId()));
        return examDetail;
    }

    private List<Location> getExamCenters(List<Instance> instances) {
        if (!CollectionUtils.isEmpty(instances)) {
            int admissonYear = 0;
            List<String> examCenters = null;
            for (Instance instance : instances) {
                if (instance.getAdmissionYear() != null && instance.getAdmissionYear() > admissonYear
                        && !CollectionUtils.isEmpty(instance.getExamCenters())) {
                    admissonYear = instance.getAdmissionYear();
                    examCenters = instance.getExamCenters();
                }
            }
            if (!CollectionUtils.isEmpty(examCenters)) {
                List<Location> locationList = new ArrayList<>();
                examCenters.forEach(examCenter -> {
                    String[] locationArr = examCenter.split(",");
                    if (locationArr.length == 2) {
                        locationList.add(Location.builder().city(locationArr[0]).state(locationArr[1]).build());
                    }
                });
                return locationList;
            }
        }
        return null;
    }
}
