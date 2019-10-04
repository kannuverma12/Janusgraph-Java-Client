package com.paytm.digital.education.explore.service.helper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paytm.digital.education.explore.database.entity.Exam;
import com.paytm.digital.education.explore.database.entity.Instance;
import com.paytm.digital.education.explore.database.entity.SectionConfiguration;
import com.paytm.digital.education.explore.enums.ExamSectionType;
import com.paytm.digital.education.explore.response.dto.detail.ExamDetail;
import com.paytm.digital.education.explore.response.dto.detail.Section;
import com.paytm.digital.education.explore.response.dto.detail.SectionDataHolder;
import com.paytm.digital.education.explore.response.dto.detail.Syllabus;
import com.paytm.digital.education.explore.response.dto.detail.Topic;
import com.paytm.digital.education.explore.response.dto.detail.Unit;
import com.paytm.digital.education.explore.utility.CommonUtil;
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

import static com.paytm.digital.education.explore.constants.ExploreConstants.SECTION;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SECTION_PLACEHOLDER;
import static com.paytm.digital.education.explore.constants.ExploreConstants.ZERO;

@Service
@Slf4j
@AllArgsConstructor
public class ExamSectionHelper {

    private ExamInstanceHelper examInstanceHelper;

    public void addDataPerSection(Exam exam, ExamDetail examDetail, List<String> sections,
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
        examDetail.setSectionDataHolders(responseSections);
    }

    private boolean setSectionData(Exam exam, ExamDetail examDetail, ExamSectionType sectionName,
            Instance nearestInstance, Map<String, Instance> subExamInstances, boolean syllabusFlg) {
        switch (sectionName) {
            case CUTOFF:
                examDetail.setCutoff(exam.getCutoff());
                return StringUtils.isNotBlank(exam.getCutoff());
            case COUNSELLING:
                examDetail.setDocumentsRequiredAtCounselling(exam.getDocumentsCounselling());
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
                .examSectionType(sectionConfiguration.getType())
                .displayName(sectionConfiguration.getDisplayName()).build();
    }

}
