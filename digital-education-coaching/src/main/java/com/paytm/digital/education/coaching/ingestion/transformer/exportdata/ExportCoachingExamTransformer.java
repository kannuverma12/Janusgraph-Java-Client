package com.paytm.digital.education.coaching.ingestion.transformer.exportdata;

import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingExamForm;
import com.paytm.digital.education.database.entity.CoachingExamEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.EMPTY_STRING;

public class ExportCoachingExamTransformer {

    public static List<CoachingExamForm> convert(final List<CoachingExamEntity> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return new ArrayList<>();
        }
        return entityList.stream()
                .map(entity -> CoachingExamForm.builder()
                        .coachingExamId(entity.getCoachingExamId())
                        .instituteId(entity.getInstituteId())
                        .examType(entity.getExamType().getText())
                        .examName(entity.getExamName())
                        .examDescription(entity.getExamDescription())
                        .streamIds(entity.getStreamIds() == null
                                ? EMPTY_STRING : StringUtils.join(entity.getStreamIds(), ","))
                        .courseIds(entity.getCourseIds() == null
                                ? EMPTY_STRING : StringUtils.join(entity.getCourseIds(), ","))
                        .examDuration(entity.getCourseIds() == null
                                ? EMPTY_STRING : entity.getExamDuration())
                        .maximumMarks(entity.getMaximumMarks())
                        .examDates(entity.getExamDate() == null
                                ? EMPTY_STRING : StringUtils.join(entity.getExamDate(), ","))
                        .eligibility(entity.getEligibility())
                        .numberOfQuestions(entity.getQuestionCount())
                        .globalPriority(entity.getPriority())
                        .statusActive(ExportCommonTransformer.convertBooleanToString(
                                entity.getIsEnabled()))
                        .build())
                .collect(Collectors.toList());
    }
}
