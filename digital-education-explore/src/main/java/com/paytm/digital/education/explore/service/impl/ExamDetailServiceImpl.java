package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.ExamPaytmKeys;
import com.paytm.digital.education.database.entity.Instance;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.dto.detail.Event;
import com.paytm.digital.education.dto.detail.ImportantDate;
import com.paytm.digital.education.dto.detail.Section;
import com.paytm.digital.education.dto.detail.Topic;
import com.paytm.digital.education.dto.detail.Unit;
import com.paytm.digital.education.enums.Client;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.explore.response.dto.detail.ExamDetail;
import com.paytm.digital.education.explore.response.dto.detail.Location;
import com.paytm.digital.education.explore.service.helper.BannerDataHelper;
import com.paytm.digital.education.explore.service.helper.DerivedAttributesHelper;
import com.paytm.digital.education.explore.service.helper.DetailPageSectionHelper;
import com.paytm.digital.education.explore.service.helper.ExamSectionHelper;
import com.paytm.digital.education.explore.service.helper.WidgetsDataHelper;
import com.paytm.digital.education.property.reader.PropertyReader;
import com.paytm.digital.education.serviceimpl.helper.ExamDatesHelper;
import com.paytm.digital.education.serviceimpl.helper.ExamInstanceHelper;
import com.paytm.digital.education.serviceimpl.helper.ExamLogoHelper;
import com.paytm.digital.education.utility.CommonUtil;
import com.paytm.digital.education.utility.DateUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.paytm.digital.education.constant.ExploreConstants.APPLICATION;
import static com.paytm.digital.education.constant.ExploreConstants.DATA;
import static com.paytm.digital.education.constant.ExploreConstants.DD_MMM_YYYY;
import static com.paytm.digital.education.constant.ExploreConstants.DEFAULT;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_DETAIL;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_FILTER_NAMESPACE;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_ID;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_PREFIX;
import static com.paytm.digital.education.constant.ExploreConstants.EXPLORE_COMPONENT;
import static com.paytm.digital.education.constant.ExploreConstants.LINGUISTIC_MEDIUM;
import static com.paytm.digital.education.constant.ExploreConstants.LINGUISTIC_MEDIUM_NAMESPACE;
import static com.paytm.digital.education.constant.ExploreConstants.MMM_YYYY;
import static com.paytm.digital.education.constant.ExploreConstants.NON_TENTATIVE;
import static com.paytm.digital.education.constant.ExploreConstants.PRECEDENCE;
import static com.paytm.digital.education.constant.ExploreConstants.SECTION;
import static com.paytm.digital.education.constant.ExploreConstants.WEB_FORM_URI_PREFIX;
import static com.paytm.digital.education.constant.ExploreConstants.ZERO;
import static com.paytm.digital.education.enums.Client.APP;
import static com.paytm.digital.education.enums.EducationEntity.EXAM;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_EXAM_ID;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_EXAM_NAME;
import static com.paytm.digital.education.utility.DateUtil.dateToString;

@Service
@RequiredArgsConstructor
public class ExamDetailServiceImpl {

    private final CommonMongoRepository   commonMongoRepository;
    private final ExamLogoHelper          examLogoHelper;
    private final ExamInstanceHelper      examInstanceHelper;
    private final PropertyReader          propertyReader;
    private final DerivedAttributesHelper derivedAttributesHelper;
    private final DetailPageSectionHelper detailPageSectionHelper;
    private final BannerDataHelper        bannerDataHelper;
    private final WidgetsDataHelper       widgetsDataHelper;
    private final ExamSectionHelper       examSectionHelper;
    private final ExamDatesHelper         examDatesHelper;

    @Value("${exam.default.instances.for.date:2}")
    private Integer defaultNoOfInstances;

    private static int EXAM_PREFIX_LENGTH = EXAM_PREFIX.length();

    //TODO - modularize methods for caching as. Its fine as of now as userId is not being used As of now.
    @Cacheable(value = "exam_detail", keyGenerator = "customKeyGenerator")
    public ExamDetail getExamDetail(Long entityId, String examUrlKey, String fieldGroup,
            List<String> fields, Client client, boolean syllabus,
            boolean importantDates, boolean derivedAttributes, boolean examCenters,
            boolean sections,
            boolean widgets, boolean policies) throws ParseException {

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

        if (APP.equals(client)) {
            importantDates = false;
        }

        Exam exam =
                commonMongoRepository.getEntityByFields(EXAM_ID, entityId, Exam.class,
                        examFields);

        if (Objects.isNull(exam)) {
            throw new BadRequestException(INVALID_EXAM_ID, INVALID_EXAM_ID.getExternalMessage());
        }

        if (!examUrlKey
                .equals(CommonUtil.convertNameToUrlDisplayName(exam.getExamFullName()))) {
            throw new BadRequestException(INVALID_EXAM_NAME,
                    INVALID_EXAM_NAME.getExternalMessage());
        }
        return processExamDetail(exam, examFields, client, syllabus, importantDates,
                derivedAttributes, examCenters, sections, widgets, policies);
    }

    @Cacheable(value = "process_exam_detail", keyGenerator = "customKeyGenerator")
    public ExamDetail processExamDetail(Exam exam, List<String> examFields, Client client,
            boolean syllabus, boolean importantDates, boolean derivedAttributes,
            boolean examCenters, boolean sections, boolean widgets, boolean policies) {

        Instance nearestInstance =
                examInstanceHelper.getNearestInstance(exam.getInstances()).get();
        Map<String, Instance> subExamInstances =
                examInstanceHelper.getSubExamInstances(exam, nearestInstance.getInstanceId());

        return buildResponse(exam, client, syllabus, importantDates,
                derivedAttributes, examCenters, sections, widgets, policies, nearestInstance,
                subExamInstances);
    }

    @Cacheable(value = "exam_app_specific_data", keyGenerator = "customKeyGenerator")
    public void addAppSpecificData(ExamDetail examDetail, Exam exam, List<String> sections,
            boolean syllabusFlg, Instance nearestInstance,
            Map<String, Instance> subExamInstances) {
        Map<String, Object> sectionConfigurationMap =
                propertyReader.getPropertiesAsMapByKey(EXPLORE_COMPONENT, EXAM_DETAIL, SECTION);
        examSectionHelper
                .addDataPerSection(exam, examDetail, sections, nearestInstance, subExamInstances,
                        sectionConfigurationMap, syllabusFlg);
    }

    private List<Section> getSectionsFromEntitySyllabus(
            List<com.paytm.digital.education.database.entity.Syllabus> entitySyllabusList) {
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

    private void updateApplicationAndExamDates(ExamDetail examDetail,
            List<ImportantDate> importantDates) {
        if (CollectionUtils.isEmpty(importantDates)) {
            return;
        }

        boolean applicationDateFound = false;
        boolean examDateFound = false;
        for (ImportantDate importantDate : importantDates) {
            if (!CollectionUtils.isEmpty(importantDate.getUpcomingDates())) {
                for (Event event : importantDate.getUpcomingDates()) {
                    if (APPLICATION.equalsIgnoreCase(event.getType()) && !applicationDateFound) {
                        if (NON_TENTATIVE.equalsIgnoreCase(event.getCertainity())) {
                            examDetail.setApplicationOpening(dateToString(event.getDateStartRange(),
                                    DD_MMM_YYYY));
                            if (Objects.nonNull(event.getDateEndRange())) {
                                examDetail
                                        .setApplicationClosing(dateToString(event.getDateEndRange(),
                                                DD_MMM_YYYY));
                            }
                        } else {
                            examDetail.setApplicationMonth(event.getMonthDate());
                        }
                    } else if (EXAM.name().equalsIgnoreCase(event.getType()) && !examDateFound) {
                        if (NON_TENTATIVE.equalsIgnoreCase(event.getCertainity())) {
                            examDetail.setExamStartDate(
                                    dateToString(event.getDateStartRange(),
                                            DD_MMM_YYYY));
                            if (Objects.nonNull(event.getDateEndRange())) {
                                examDetail.setExamEndDate(
                                        dateToString(event.getDateEndRange(),
                                                DD_MMM_YYYY));
                            }
                        }
                    }
                }
            }
            if (applicationDateFound && examDateFound) {
                break;
            }
        }
    }

    private void addApplicationAndExamDatesToResponse(ExamDetail examDetail,
            List<Event> importantDates) {
        for (int i = 0; i < importantDates.size(); i++) {
            if (importantDates.get(i).getType().equalsIgnoreCase(APPLICATION)) {
                if (importantDates.get(i).getCertainity() != null
                        && importantDates.get(i).getCertainity().equalsIgnoreCase(NON_TENTATIVE)) {
                    if (importantDates.get(i).getDateEndRange() != null) {
                        examDetail.setApplicationOpening(
                                dateToString(importantDates.get(i).getDateStartRange(),
                                        DD_MMM_YYYY));
                        examDetail.setApplicationClosing(dateToString(
                                importantDates.get(i).getDateEndRange(), DD_MMM_YYYY));
                    } else {
                        examDetail.setApplicationOpening(
                                dateToString(importantDates.get(i).getDateStartRange(),
                                        DD_MMM_YYYY));
                    }

                } else {
                    examDetail.setApplicationMonth(importantDates.get(i).getMonthDate());
                }
            } else if (importantDates.get(i).getType().equalsIgnoreCase(EXAM.name())) {
                if (importantDates.get(i).getCertainity() != null
                        && importantDates.get(i).getCertainity().equalsIgnoreCase(NON_TENTATIVE)) {
                    if (importantDates.get(i).getDateEndRange() != null) {
                        examDetail.setExamStartDate(
                                dateToString(importantDates.get(i).getDateStartRange(),
                                        DD_MMM_YYYY));
                        examDetail.setExamEndDate(
                                dateToString(importantDates.get(i).getDateEndRange(),
                                        DD_MMM_YYYY));
                    } else {
                        examDetail.setExamStartDate(
                                dateToString(importantDates.get(i).getDateStartRange(),
                                        DD_MMM_YYYY));
                    }

                } else {
                    examDetail
                            .setExamMonth(DateUtil.formatDateString(
                                    importantDates.get(i).getMonthDate(), MMM_YYYY, DD_MMM_YYYY));
                }
            }
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

    @Cacheable(value = "exam_web_specific_data", keyGenerator = "customKeyGenerator")
    public void addWebSpecificData(ExamDetail examDetail, Exam exam, boolean derivedAttributes,
            boolean sectionsFlag,
            Client client, boolean widgets) {
        examDetail.setDocumentsRequiredAtExam(exam.getDocumentsExam());
        examDetail.setDocumentsRequiredAtCounselling(exam.getDocumentsCounselling());
        examDetail.setAdmitCard(exam.getAdmitCard());
        examDetail.setEligibility(exam.getEligibility());
        examDetail.setApplicationForm(exam.getApplicationForm());
        examDetail.setExamPattern(exam.getExamPattern());
        examDetail.setResult(exam.getResult());
        examDetail.setCutoff(exam.getCutoff());
        if (!CollectionUtils.isEmpty(exam.getSubExams())) {
            examDetail.setDurationInHour(exam.getSubExams().get(0).getDurationHours());
        }
        if (examDetail.getDurationInHour() == null) {
            examDetail.setDurationInHour(exam.getExamDuration());
        }
        String entityName = EXAM.name().toLowerCase();
        Map<String, Object> highlights = new HashMap<>();
        highlights.put(entityName, exam);
        highlights.put(LINGUISTIC_MEDIUM, examDetail.getLinguisticMedium());
        if (derivedAttributes) {
            examDetail.setDerivedAttributes(
                    derivedAttributesHelper.getDerivedAttributes(highlights,
                            entityName, client));
        }
        if (sectionsFlag) {
            examDetail.setSections(detailPageSectionHelper.getSectionOrder(entityName, null));
        }
        examDetail.setBanners(bannerDataHelper.getBannerData(entityName, null));
        if (widgets) {
            examDetail.setWidgets(widgetsDataHelper.getWidgets(entityName, exam.getExamId(),
                    getDomainName(exam.getDomains())
            ));
        }
    }

    @Cacheable(value = "exam_build_response", keyGenerator = "customKeyGenerator")
    public  ExamDetail buildResponse(Exam exam, Client client, boolean syllabus,
            boolean importantDatesflag, boolean derivedAttributes, boolean examCenters,
            boolean sectionsFlag, boolean widgets, boolean policies, Instance nearestInstance,
            Map<String, Instance> subExamInstances) {
        ExamDetail examDetail = new ExamDetail();
        addCommonData(examDetail, exam, nearestInstance, subExamInstances, syllabus,
                importantDatesflag, examCenters, policies);
        if (APP.equals(client)) {
            List<String> sectionsList =
                    detailPageSectionHelper.getSectionOrder(EXAM.name().toLowerCase(), client);
            addAppSpecificData(examDetail, exam, sectionsList, syllabus, nearestInstance,
                    subExamInstances);
        } else {
            addWebSpecificData(examDetail, exam, derivedAttributes, sectionsFlag, client, widgets);
        }
        return examDetail;
    }

    private void addCommonData(ExamDetail examResponse, Exam exam, Instance nearestInstance,
            Map<String, Instance> subExamInstances, boolean syllabusflg, boolean importantDatesFlg,
            boolean examCentersFlg, boolean policies) {
        examResponse.setExamId(exam.getExamId());
        examResponse.setAbout(exam.getAboutExam());
        examResponse
                .setUrlDisplayName(CommonUtil.convertNameToUrlDisplayName(exam.getExamFullName()));
        examResponse.setExamFullName(exam.getExamFullName());
        examResponse.setExamShortName(exam.getExamShortName());
        if (!CollectionUtils.isEmpty(exam.getLinguisticMediumExam())) {
            setLanguageFromLanguageCodes(examResponse, exam.getLinguisticMediumExam());
        }
        examResponse.setExamLevel(exam.getLevelOfExam());
        examResponse.setLogoUrl(examLogoHelper.getExamLogoUrl(exam.getExamId(), exam.getLogo()));
        if (examCentersFlg) {
            List<Location> examCenters = getExamCenters(nearestInstance);
            if (!CollectionUtils.isEmpty(examCenters)) {
                examResponse.setExamCenters(examCenters);
                examResponse.setCentersCount(examCenters.size());
            }
        }
        if (importantDatesFlg) {
            List<ImportantDate> importantDates =
                    examDatesHelper.getImportantDates(exam, defaultNoOfInstances);
            if (!CollectionUtils.isEmpty(importantDates)) {
                examResponse.setImportantDates(importantDates);
                updateApplicationAndExamDates(examResponse, importantDates);
            }
        }
        if (Objects.nonNull(exam.getPaytmKeys())) {
            addPaytmKeys(examResponse, exam.getPaytmKeys());

            if (policies) {
                examResponse.setTermsAndConditions(exam.getPaytmKeys().getTermsAndConditions());
                examResponse.setDisclaimer(exam.getPaytmKeys().getDisclaimer());
                examResponse.setPrivacyPolicies(exam.getPaytmKeys().getPrivacyPolicies());
                examResponse
                        .setRegistrationGuidelines(exam.getPaytmKeys().getRegistrationGuidelines());
            }
        }
        if (syllabusflg) {
            List<com.paytm.digital.education.dto.detail.Syllabus> syllabus =
                    examInstanceHelper.getSyllabus(nearestInstance, subExamInstances, exam);
            if (!CollectionUtils.isEmpty(syllabus)) {
                examResponse.setSyllabus(syllabus);
            }
        }
    }

    private void addPaytmKeys(ExamDetail examDetail, ExamPaytmKeys examPaytmKeys) {
        examDetail.setCollegePredictorPid(examPaytmKeys.getCollegePredictorId());
        examDetail.setFormId(examPaytmKeys.getFormId());
        if (StringUtils.isNotBlank(examPaytmKeys.getWebFormUriPrefix())) {
            examDetail.setAdditionalProperties(new HashMap<>());
            examDetail.getAdditionalProperties()
                    .put(WEB_FORM_URI_PREFIX, examPaytmKeys.getWebFormUriPrefix());
        }
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

    private List<Location> getExamCenters(Instance nearestInstance) {
        if (!CollectionUtils.isEmpty(nearestInstance.getExamCenters())) {
            List<Location> locationList = new ArrayList<>();
            nearestInstance.getExamCenters().forEach(examCenter -> {
                String[] locationArr = examCenter.split(",");
                if (locationArr.length == 2) {
                    locationList
                            .add(Location.builder().city(locationArr[0]).state(locationArr[1])
                                    .build());
                }
            });
            return locationList;
        }
        return null;
    }

}
