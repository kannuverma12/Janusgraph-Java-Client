package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.annotation.EduCache;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.ExamPaytmKeys;
import com.paytm.digital.education.database.entity.Instance;
import com.paytm.digital.education.database.entity.SubExam;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.dto.detail.Event;
import com.paytm.digital.education.enums.Client;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.explore.response.dto.detail.ExamDetail;
import com.paytm.digital.education.explore.response.dto.detail.Location;
import com.paytm.digital.education.explore.service.helper.BannerDataHelper;
import com.paytm.digital.education.explore.service.helper.DerivedAttributesHelper;
import com.paytm.digital.education.explore.service.helper.DetailPageSectionHelper;
import com.paytm.digital.education.explore.service.helper.ExamSectionHelper;
import com.paytm.digital.education.explore.service.helper.SimilarExamsHelper;
import com.paytm.digital.education.property.reader.PropertyReader;
import com.paytm.digital.education.serviceimpl.helper.ExamInstanceHelper;
import com.paytm.digital.education.serviceimpl.helper.ExamLogoHelper;
import com.paytm.digital.education.utility.CommonUtil;
import com.paytm.digital.education.utility.DateUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.paytm.digital.education.constant.ExploreConstants.APPLICATION;
import static com.paytm.digital.education.constant.ExploreConstants.DD_MMM_YYYY;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_DETAIL;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_FILTER_NAMESPACE;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_ID;
import static com.paytm.digital.education.constant.ExploreConstants.EXAM_PREFIX;
import static com.paytm.digital.education.constant.ExploreConstants.EXPLORE_COMPONENT;
import static com.paytm.digital.education.constant.ExploreConstants.LINGUISTIC_MEDIUM;
import static com.paytm.digital.education.constant.ExploreConstants.LINGUISTIC_MEDIUM_NAMESPACE;
import static com.paytm.digital.education.constant.ExploreConstants.MMM_YYYY;
import static com.paytm.digital.education.constant.ExploreConstants.NON_TENTATIVE;
import static com.paytm.digital.education.constant.ExploreConstants.SECTION;
import static com.paytm.digital.education.constant.ExploreConstants.WEB_FORM_URI_PREFIX;
import static com.paytm.digital.education.enums.Client.APP;
import static com.paytm.digital.education.enums.EducationEntity.EXAM;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_EXAM_ID;
import static com.paytm.digital.education.mapping.ErrorEnum.INVALID_EXAM_NAME;

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
    private ExamSectionHelper        examSectionHelper;
    private SimilarExamsHelper       similarExamsHelper;

    private static int EXAM_PREFIX_LENGTH = EXAM_PREFIX.length();

    @EduCache(cache = "exam_detail")
    public ExamDetail getExamDetail(Long entityId, String examUrlKey, String fieldGroup,
            List<String> fields, Client client, boolean syllabus,
            boolean importantDates, boolean derivedAttributes, boolean examCenters,
            boolean sections,
            boolean widgets, boolean policies) {

        // TODO: fields are not being supported currently. Part of discussion
        List<String> groupFields =
                commonMongoRepository.getFieldsByGroup(Exam.class, fieldGroup);
        List<String> examFields = new ArrayList<>();
        if (!CollectionUtils.isEmpty(groupFields)) {
            for (String requestedField : groupFields) {
                if (requestedField.contains(EXAM_PREFIX)) {
                    examFields.add(requestedField
                            .substring(EXAM_PREFIX_LENGTH));
                }
            }
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

    /**
     * TODO:-
     * Problem:- A cached method is calling another cached method in same service.
     * Cache by pass issue can arise.
     * Possible solutions:-
     *   1. Is caching this method required ?
     *   2. Refactor to move this method out
     *   3. Autowire self
     */
    @Cacheable(value = "process_exam_detail", keyGenerator = "customKeyGenerator")
    public ExamDetail processExamDetail(Exam exam, List<String> examFields, Client client,
            boolean syllabus, boolean importantDates, boolean derivedAttributes,
            boolean examCenters, boolean sections, boolean widgets, boolean policies) {

        Instance nearestInstance =
                examInstanceHelper.getNearestInstance(exam.getInstances()).get();
        Map<String, Instance> subExamInstances =
                getSubExamInstances(exam, nearestInstance.getInstanceId());

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

    private Map<String, Instance> getSubExamInstances(Exam exam, int parentInstanceId) {
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

    private void addApplicationAndExamDatesToResponse(ExamDetail examDetail,
            List<Event> importantDates) {
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
                    examDetail.setApplicationMonth(importantDates.get(i).getMonthDate());
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
    public void addWebSpecificData(ExamDetail examDetail, Exam exam, boolean sectionsFlag,
            boolean widgets) {
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
        if (sectionsFlag) {
            examDetail.setSections(detailPageSectionHelper.getSectionOrder(entityName, null));
        }
        examDetail.setBanners(bannerDataHelper.getBannerData(entityName, null));
        if (widgets) {
            examDetail.setWidgets(similarExamsHelper.getWidgetsData(exam));
        }
    }

    @Cacheable(value = "exam_build_response", keyGenerator = "customKeyGenerator")
    public  ExamDetail buildResponse(Exam exam, Client client, boolean syllabus,
            boolean importantDatesflag, boolean derivedAttributes, boolean examCenters,
            boolean sectionsFlag, boolean widgets, boolean policies, Instance nearestInstance,
            Map<String, Instance> subExamInstances) {
        ExamDetail examDetail = new ExamDetail();
        addCommonData(examDetail, exam, nearestInstance, subExamInstances, syllabus,
                importantDatesflag, examCenters, policies, client, derivedAttributes);
        if (APP.equals(client)) {
            List<String> sectionsList =
                    detailPageSectionHelper.getSectionOrder(EXAM.name().toLowerCase(), client);
            addAppSpecificData(examDetail, exam, sectionsList, syllabus, nearestInstance,
                    subExamInstances);
        } else {
            addWebSpecificData(examDetail, exam, sectionsFlag, widgets);
        }
        return examDetail;
    }

    private void addCommonData(ExamDetail examResponse, Exam exam, Instance nearestInstance,
            Map<String, Instance> subExamInstances, boolean syllabusflg, boolean importantDatesFlg,
            boolean examCentersFlg, boolean policies, Client client, boolean derivedAttributes) {
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
            List<Event> importantDates =
                    examInstanceHelper.getImportantDates(exam, nearestInstance, subExamInstances);
            if (!CollectionUtils.isEmpty(importantDates)) {
                examResponse.setImportantDates(importantDates);
                addApplicationAndExamDatesToResponse(examResponse, importantDates);
            }
        }
        if (Objects.nonNull(exam.getPaytmKeys())) {
            addPaytmKeys(examResponse, exam.getPaytmKeys());

            if (policies) {
                examResponse.setTermsAndConditions(exam.getPaytmKeys().getTermsAndConditions());
                examResponse.setDisclaimer(exam.getPaytmKeys().getDisclaimer());
                examResponse.setPrivacyPolicies(exam.getPaytmKeys().getPrivacyPolicies());
                examResponse.setRegistrationGuidelines(exam.getPaytmKeys().getRegistrationGuidelines());
            }
        }
        if (syllabusflg) {
            List<com.paytm.digital.education.dto.detail.Syllabus> syllabus =
                    examInstanceHelper.getSyllabus(nearestInstance, subExamInstances, exam);
            if (!CollectionUtils.isEmpty(syllabus)) {
                examResponse.setSyllabus(syllabus);
            }
        }
        String entityName = EXAM.name().toLowerCase();
        Map<String, Object> highlights = new HashMap<>();
        highlights.put(entityName, exam);
        highlights.put(LINGUISTIC_MEDIUM, examResponse.getLinguisticMedium());
        if (derivedAttributes) {
            examResponse.setDerivedAttributes(
                    derivedAttributesHelper.getDerivedAttributes(highlights,
                            entityName, client));
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
