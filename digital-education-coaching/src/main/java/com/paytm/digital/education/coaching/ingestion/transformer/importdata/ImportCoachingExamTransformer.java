package com.paytm.digital.education.coaching.ingestion.transformer.importdata;

import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingExamForm;
import com.paytm.digital.education.coaching.producer.model.request.CoachingExamDataRequest;
import com.paytm.digital.education.enums.ExamType;

public class ImportCoachingExamTransformer {

    public static CoachingExamDataRequest convert(final CoachingExamForm form) {
        if (null == form) {
            return null;
        }
        return CoachingExamDataRequest.builder()
                .coachingExamId(form.getCoachingExamId())
                .instituteId(form.getInstituteId())
                .examType(ExamType.fromString(form.getExamType()))
                .examName(form.getExamName())
                .examDescription(form.getExamDescription())
                .courseIds(ImportCommonTransformer.convertStringToListOfLong(
                        form.getCourseIds()))
                .streamIds(ImportCommonTransformer.convertStringToListOfLong(
                        form.getStreamIds()))
                .examDuration(form.getExamDuration())
                .maximumMarks(form.getMaximumMarks())
                .examDate(null)
                .eligibility(form.getEligibility())
                .questionCount(form.getNumberOfQuestions())
                .priority(form.getGlobalPriority())
                .isEnabled(ImportCommonTransformer.convertStringToBoolean(
                        form.getStatusActive()))
                .build();
    }
}
