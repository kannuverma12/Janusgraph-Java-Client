package com.paytm.digital.education.explore.service.impl;

import static com.paytm.digital.education.explore.constants.ExploreConstants.APPLICATION;
import static com.paytm.digital.education.explore.constants.ExploreConstants.DATA;
import static com.paytm.digital.education.explore.constants.ExploreConstants.DD_MMM_YYYY;
import static com.paytm.digital.education.explore.constants.ExploreConstants.DEFAULT;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_FILTER_NAMESPACE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_ID;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_PREFIX;
import static com.paytm.digital.education.explore.constants.ExploreConstants.EXPLORE_COMPONENT;
import static com.paytm.digital.education.explore.constants.ExploreConstants.LINGUISTIC_MEDIUM;
import static com.paytm.digital.education.explore.constants.ExploreConstants.LINGUISTIC_MEDIUM_NAMESPACE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.MMM_YYYY;
import static com.paytm.digital.education.explore.constants.ExploreConstants.NON_TENTATIVE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.PRECEDENCE;
import static com.paytm.digital.education.explore.constants.ExploreConstants.YYYY_MM;
import static com.paytm.digital.education.explore.constants.ExploreConstants.ZERO;
import static com.paytm.digital.education.explore.enums.EducationEntity.EXAM;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_EXAM_ID;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_EXAM_NAME;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;

import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.explore.database.entity.Exam;
import com.paytm.digital.education.explore.database.entity.ExamPaytmKeys;
import com.paytm.digital.education.explore.database.entity.Instance;
import com.paytm.digital.education.explore.database.entity.SubExam;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.enums.Client;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.response.dto.common.CTA;
import com.paytm.digital.education.explore.response.dto.detail.ExamDetail;
import com.paytm.digital.education.explore.response.dto.detail.Section;
import com.paytm.digital.education.explore.response.dto.detail.Event;
import com.paytm.digital.education.explore.response.dto.detail.Unit;
import com.paytm.digital.education.explore.response.dto.detail.Syllabus;
import com.paytm.digital.education.explore.response.dto.detail.Topic;
import com.paytm.digital.education.explore.response.dto.detail.Location;
import com.paytm.digital.education.explore.service.helper.ExamLogoHelper;
import com.paytm.digital.education.explore.service.helper.ExamInstanceHelper;
import com.paytm.digital.education.explore.service.helper.DerivedAttributesHelper;
import com.paytm.digital.education.explore.service.helper.DetailPageSectionHelper;
import com.paytm.digital.education.explore.service.helper.BannerDataHelper;
import com.paytm.digital.education.explore.service.helper.SubscriptionDetailHelper;
import com.paytm.digital.education.explore.service.helper.WidgetsDataHelper;
import com.paytm.digital.education.explore.service.helper.CTAHelper;
import com.paytm.digital.education.explore.service.helper.LeadDetailHelper;
import com.paytm.digital.education.explore.utility.CommonUtil;
import com.paytm.digital.education.property.reader.PropertyReader;
import com.paytm.digital.education.utility.DateUtil;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ExamDetailServiceImpl {

    private CommonMongoRepository    commonMongoRepository;
    private ExamLogoHelper           examLogoHelper;
    private ExamInstanceHelper       examInstanceHelper;
    private PropertyReader           propertyReader;
    private DerivedAttributesHelper  derivedAttributesHelper;
    private DetailPageSectionHelper  detailPageSectionHelper;
    private BannerDataHelper         bannerDataHelper;
    private WidgetsDataHelper        widgetsDataHelper;
    private LeadDetailHelper         leadDetailHelper;
    private CTAHelper                ctaHelper;
    private SubscriptionDetailHelper subscriptionDetailHelper;

    private static int EXAM_PREFIX_LENGTH = EXAM_PREFIX.length();

    public ExamDetail getDetail(Long entityId, String examUrlKey, Long userId,
            String fieldGroup, List<String> fields, Client client, Boolean syllabus,
            Boolean importantDates, Boolean derivedAttributes, Boolean examCenters, Boolean sections,
            Boolean widgets) throws ParseException {
        // fields are not being supported currently. Part of discussion

        ExamDetail examDetail = getExamDetail(entityId, examUrlKey, fieldGroup, fields, client,
                syllabus, importantDates, derivedAttributes, examCenters, sections, widgets);
        if (userId != null && userId > 0) {
            updateInterested(examDetail, userId);
            updateShortlist(examDetail, userId);
        }
        List<CTA> ctas = ctaHelper.buildCTA(examDetail, client);
        if (!CollectionUtils.isEmpty(ctas)) {
            examDetail.setCtaList(ctas);
        }
        return examDetail;
    }

    //TODO - modularize methods for caching as. Its fine as of now as userId is not being used As of now.
    @Cacheable(value = "exam_detail")
    public ExamDetail getExamDetail(Long entityId, String examUrlKey, String fieldGroup,
            List<String> fields, Client client, Boolean syllabus,
            Boolean importantDates, Boolean derivedAttributes, Boolean examCenters, Boolean sections,
            Boolean widgets) throws ParseException {

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
            if (!examUrlKey
                    .equals(CommonUtil.convertNameToUrlDisplayName(exam.getExamFullName()))) {
                throw new BadRequestException(INVALID_EXAM_NAME,
                        INVALID_EXAM_NAME.getExternalMessage());
            }
            return processExamDetail(exam, examFields, client, syllabus, importantDates,
                    derivedAttributes, examCenters, sections, widgets);
        }
        throw new BadRequestException(INVALID_EXAM_ID,
                INVALID_EXAM_ID.getExternalMessage());
    }

    private ExamDetail processExamDetail(Exam exam, List<String> examFields, Client client,
            Boolean syllabus, Boolean importantDates, Boolean derivedAttributes, Boolean examCenters,
            Boolean sections, Boolean widgets)
            throws ParseException {
        ExamDetail examDetail = buildResponse(exam, client, syllabus, importantDates,
                derivedAttributes, examCenters, sections, widgets);
        return examDetail;
    }

    private List<Section> getSectionsFromEntitySyllabus(
            List<com.paytm.digital.education.explore.database.entity.Syllabus> entitySyllabusList) {
        List<Section> sectionList = new ArrayList<>();
        entitySyllabusList.forEach(entitySection -> {
            List<Unit> units = new ArrayList<>();
            entitySection.getUnits().forEach(entityUnit -> {
                String unitName = entityUnit.getName();
                if (!unitName.equals(ZERO)) {
                    List<Topic> topics = new ArrayList<>();
                    entityUnit.getTopics().forEach(entityTopic -> {
                        String topicName = entityTopic.getName();
                        if (!topicName.equals(ZERO)) {
                            Topic topic = new Topic(topicName);
                            topics.add(topic);
                        }
                    });
                    Unit unit = new Unit(unitName, topics);
                    units.add(unit);
                }
            });
            Section section = new Section(entitySection.getSubjectName(), units);
            sectionList.add(section);
        });
        return sectionList;
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
                            .addAll(examInstanceHelper
                                    .convertEntityEventToResponse(subExam.getSubExamName(),
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

    private ExamDetail buildResponse(Exam exam, Client client, Boolean syllabus,
            Boolean importantDatesflag, Boolean derivedAttributes, Boolean examCenters,
            Boolean sectionsFlag, Boolean widgets) throws ParseException {
        ExamDetail examDetail = new ExamDetail();
        examDetail.setExamId(exam.getExamId());
        examDetail.setAbout(exam.getAboutExam());
        examDetail.setExamId(exam.getExamId());
        examDetail
                .setUrlDisplayName(CommonUtil.convertNameToUrlDisplayName(exam.getExamFullName()));
        examDetail.setExamFullName(exam.getExamFullName());
        examDetail.setExamShortName(exam.getExamShortName());
        if (!CollectionUtils.isEmpty(exam.getLinguisticMediumExam())) {
            setLanguageFromLanguageCodes(examDetail, exam.getLinguisticMediumExam());
        }
        examDetail.setExamLevel(exam.getLevelOfExam());
        examDetail.setDocumentsRequiredAtExam(exam.getDocumentsExam());
        examDetail.setDocumentsRequiredAtCounselling(exam.getDocumentsCounselling());
        examDetail.setAdmitCard(exam.getAdmitCard());
        examDetail.setAnswerKey("");
        examDetail.setCounselling("");
        examDetail.setEligibility(exam.getEligibility());
        examDetail.setApplicationForm(exam.getApplicationForm());
        examDetail.setExamPattern(exam.getExamPattern());
        examDetail.setResult(exam.getResult());
        examDetail.setCutoff(exam.getCutoff());
        examDetail.setLogoUrl(examLogoHelper.getExamLogoUrl(exam.getExamId(), exam.getLogo()));
        if ((Objects.nonNull(examCenters) && examCenters) || Objects.isNull(examCenters)) {
            examDetail.setExamCenters(getExamCenters(exam.getInstances()));
        }
        List<Event> importantDates = new ArrayList<>();
        Optional<Instance> nearestInstance = empty();
        if ((Objects.nonNull(importantDatesflag) && importantDatesflag) || Objects
                .isNull(importantDatesflag)) {
            if (!CollectionUtils.isEmpty(exam.getInstances())) {
                nearestInstance =
                        examInstanceHelper.getNearestInstance(exam.getInstances());
                if (nearestInstance.isPresent()) {
                    List<com.paytm.digital.education.explore.database.entity.Event> events =
                            nearestInstance.map(Instance::getEvents).orElse(emptyList());
                    int centersCount =
                            nearestInstance.map(Instance::getExamCenters).map(List::size).orElse(0);
                    examDetail.setCentersCount(centersCount);
                    importantDates
                            .addAll(examInstanceHelper
                                    .convertEntityEventToResponse(exam.getExamFullName(), events));
                }
            }
        }
        if (!CollectionUtils.isEmpty(exam.getSubExams()) && nearestInstance.isPresent()) {
            int parentInstanceId = nearestInstance.get().getInstanceId();
            examDetail.setDurationInHour(exam.getSubExams().get(0).getDurationHours());
            addSubExamData(parentInstanceId, exam.getSubExams(), examDetail, importantDates);
        }
        if ((Objects.nonNull(importantDatesflag) && importantDatesflag) || Objects
                .isNull(importantDatesflag)) {
            examDetail.setImportantDates(importantDates);
        }
        if ((Objects.nonNull(syllabus) && syllabus) || Objects.isNull(syllabus)) {
            if (CollectionUtils.isEmpty(examDetail.getSyllabus())) {
                List<Syllabus> syllabusList = new ArrayList<>();
                List<com.paytm.digital.education.explore.database.entity.Syllabus>
                        syllabusListFromInstance
                        = nearestInstance.map(Instance::getSyllabusList).orElse(emptyList());
                if (!CollectionUtils.isEmpty(syllabusListFromInstance)) {
                    List<Section> sections =
                            getSectionsFromEntitySyllabus(syllabusListFromInstance);
                    syllabusList.add(new Syllabus(exam.getExamFullName(), sections));
                } else if (!CollectionUtils.isEmpty(exam.getSyllabus())) {
                    List<Section> sections = getSectionsFromEntitySyllabus(exam.getSyllabus());
                    syllabusList.add(new Syllabus(exam.getExamFullName(), sections));
                }
                examDetail.setSyllabus(syllabusList);
            }
        }
        if (examDetail.getDurationInHour() == null) {
            examDetail.setDurationInHour(exam.getExamDuration());
        }
        String entityName = EXAM.name().toLowerCase();
        Map<String, Object> highlights = new HashMap<>();
        highlights.put(entityName, exam);
        highlights.put(LINGUISTIC_MEDIUM, examDetail.getLinguisticMedium());
        if ((Objects.nonNull(derivedAttributes) && derivedAttributes) || Objects
                .isNull(derivedAttributes)) {
            examDetail.setDerivedAttributes(
                    derivedAttributesHelper.getDerivedAttributes(highlights,
                            entityName, client));
        }
        addDatesToResponse(examDetail, importantDates);
        if ((Objects.nonNull(sectionsFlag) && sectionsFlag) || Objects.isNull(sectionsFlag)) {
            examDetail.setSections(detailPageSectionHelper.getSectionOrder(entityName, null));
        }

        examDetail.setBanners(bannerDataHelper.getBannerData(entityName, null));

        if (Objects.nonNull(exam.getPaytmKeys())) {
            ExamPaytmKeys examPaytmKeys = exam.getPaytmKeys();
            examDetail.setCollegePredictorPid(examPaytmKeys.getCollegePredictorId());
            examDetail.setFormId(examPaytmKeys.getFormId());
        }
        if ((Objects.nonNull(widgets) && widgets) || Objects.isNull(widgets)) {
            examDetail.setWidgets(widgetsDataHelper.getWidgets(entityName, exam.getExamId(),
                    getDomainName(exam.getDomains())
            ));
        }

        return examDetail;
    }

    private void updateShortlist(ExamDetail examDetail,
            Long userId) {
        List<Long> examIds = new ArrayList<>();
        examIds.add(examDetail.getExamId());

        List<Long> subscribedEntities = subscriptionDetailHelper
                .getSubscribedEntities(EXAM, userId, examIds);

        examDetail.setShortlisted(!CollectionUtils.isEmpty(subscribedEntities));
    }

    private String getDomainName(List<String> domains) {
        int noOfDomains = domains.size();
        if (noOfDomains == 0) {
            // when exam is not associated with any domain
            return DEFAULT;
        } else if (noOfDomains == 1) {
            // when exam is associated with only one domain
            return ((isDomainExistInDefineList(domains.get(0))) ? domains.get(0) : DEFAULT);
        } else {
            // when exam is associated with multiple domains
            return findHigherPrecedenceDomain(domains);
        }
    }

    /*
     ** Find the domain whose similar exams will be displayed when exam is associated with
     ** multiple domains.
     */
    private String findHigherPrecedenceDomain(List<String> domains) {
        List<String> domainList = getDefinedDomainList();
        for (String domain : domainList) {
            if (domains.contains(domain)) {
                return domain;
            }
        }
        return DEFAULT;
    }

    /*
     ** Check whether the domain exists in the defined list of domains
     */
    private boolean isDomainExistInDefineList(String domain) {
        List<String> domainList = getDefinedDomainList();
        if (domainList.contains(domain)) {
            return true;
        }
        return false;
    }

    /*
     ** Get the defined exam domains list
     */
    @Cacheable(value = "exam_domain_list")
    public List<String> getDefinedDomainList() {
        Map<String, Object> propertyMap = propertyReader
                .getPropertiesAsMapByKey(EXPLORE_COMPONENT, EXAM.name().toLowerCase(),
                        PRECEDENCE);
        return (List<String>) propertyMap.get(DATA);
    }

    private void updateInterested(ExamDetail examDetail, Long userId) {
        List<Long> leadEntities = leadDetailHelper
                .getInterestedLeadByEntity(EducationEntity.EXAM, userId, examDetail.getExamId());
        if (!CollectionUtils.isEmpty(leadEntities)) {
            examDetail.setInterested(true);
        }
    }

    private List<Location> getExamCenters(List<Instance> instances) {
        if (!CollectionUtils.isEmpty(instances)) {
            int admissonYear = 0;
            List<String> examCenters = null;
            for (Instance instance : instances) {
                if (instance.getAdmissionYear() != null
                        && instance.getAdmissionYear() > admissonYear
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
                        locationList
                                .add(Location.builder().city(locationArr[0]).state(locationArr[1])
                                        .build());
                    }
                });
                return locationList;
            }
        }
        return null;
    }
}
