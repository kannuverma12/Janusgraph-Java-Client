package com.paytm.digital.education.coaching.consumer.service.details.helper;

import com.paytm.digital.education.coaching.consumer.model.dto.ExamAdditionalInfo;
import com.paytm.digital.education.coaching.consumer.model.dto.SectionConfiguration;
import com.paytm.digital.education.coaching.consumer.model.response.details.GetExamDetailsResponse;
import com.paytm.digital.education.coaching.consumer.model.response.details.SectionDataHolder;
import com.paytm.digital.education.coaching.enums.ExamSectionType;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.Instance;
import com.paytm.digital.education.dto.detail.Syllabus;
import com.paytm.digital.education.serviceimpl.helper.ExamInstanceHelper;
import com.paytm.digital.education.utility.CommonUtil;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.SECTION;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.SECTION_PLACEHOLDER;
import static com.paytm.digital.education.coaching.enums.DisplayHeadings.ALL_YOU_NEED_TO_KNOW_ABOUT;

@Service
@AllArgsConstructor
@Slf4j
public class CoachingExamSectionHelper {

    private ExamInstanceHelper examInstanceHelper;

    public void addDataPerSection(Exam exam, GetExamDetailsResponse examDetail,
            List<String> sections,
            Instance nearestInstance, Map<String, Instance> subExamInstances,
            Map<String, Object> sectionConfigurationMap, boolean syllabusFlg) {

        List<SectionDataHolder> responseSections = new ArrayList<>();
        for (String sectionName : sections) {
            boolean isPresent =
                    setSectionData(exam, examDetail, ExamSectionType.valueOf(sectionName),
                            nearestInstance, subExamInstances, syllabusFlg);
            SectionConfiguration sectionConfiguration =
                    JsonUtils.convertValue(sectionConfigurationMap.get(sectionName),
                            SectionConfiguration.class);
            if (!isPresent) {
                log.error("Section data not found for {} of exam {}.", sectionName,
                        exam.getExamId());
            } else if (Objects.isNull(sectionConfiguration)) {
                log.error("Section configuration not found for {} of exam id {}.", sectionName,
                        exam.getExamId());
            } else {
                responseSections.add(getResponseSection(sectionConfiguration));
            }
        }
        ExamAdditionalInfo examAdditionalInfo = getExamAdditionalInfo(exam, responseSections);
        examDetail.setAdditionalInfo(examAdditionalInfo);
    }

    private ExamAdditionalInfo getExamAdditionalInfo(Exam exam,
            List<SectionDataHolder> responseSections) {
        return ExamAdditionalInfo.builder()
                .header(String.format(ALL_YOU_NEED_TO_KNOW_ABOUT.getValue(),
                        exam.getExamShortName()))
                .sectionsList(responseSections)
                .build();
    }

    private boolean setSectionData(Exam exam, GetExamDetailsResponse examDetail,
            ExamSectionType sectionName,
            Instance nearestInstance, Map<String, Instance> subExamInstances, boolean syllabusFlg) {
        switch (sectionName) {
            case CUTOFF:
                examDetail.setCutoff(exam.getCutoff());
                return StringUtils.isNotBlank(exam.getCutoff());
            case COUNSELLING:
                examDetail.setDocumentsCounselling(exam.getDocumentsCounselling());
                return !CollectionUtils.isEmpty(exam.getDocumentsCounselling());
            case ELIGIBILITY:
                examDetail.setEligibility(exam.getEligibility());
                return StringUtils.isNotBlank(exam.getEligibility());
            case RESULT:
                examDetail.setResult(exam.getResult());
                return StringUtils.isNotBlank(exam.getResult());
            case SYLLABUS:
                if (syllabusFlg) {
                    List<Syllabus> syllabus = examInstanceHelper
                            .getSyllabus(nearestInstance, subExamInstances, exam);
                    examDetail.setSyllabus(syllabus);
                    return !CollectionUtils.isEmpty(syllabus);
                }
                return false;
            case ADMIT_CARD:
                examDetail.setAdmitCard(exam.getAdmitCard());
                return StringUtils.isNotBlank(exam.getAdmitCard());
            case EXAM_PATTERN:
                examDetail.setExamPattern(exam.getExamPattern());
                return StringUtils.isNotBlank(exam.getExamPattern());
            case APPLICATION_FORM:
                examDetail.setApplicationForm(exam.getApplicationForm());
                return StringUtils.isNotBlank(exam.getApplicationForm());
            default:
                return false;
        }
    }

    private String getAbsoluteUrl(String relativeUrl) {
        if (StringUtils.isBlank(relativeUrl)) {
            relativeUrl = SECTION_PLACEHOLDER;
        }
        return CommonUtil.getAbsoluteUrl(relativeUrl, SECTION);
    }

    private SectionDataHolder getResponseSection(SectionConfiguration sectionConfiguration) {
        return SectionDataHolder.builder()
                .icon(getAbsoluteUrl(sectionConfiguration.getIcon()))
                .snippetText(sectionConfiguration.getShortText())
                .displayName(sectionConfiguration.getDisplayName())
                .key(sectionConfiguration.getType().getKey())
                .build();
    }

}
