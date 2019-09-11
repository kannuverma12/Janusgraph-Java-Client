package com.paytm.digital.education.coaching.ingestion.transformer;

import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingExamForm;
import com.paytm.digital.education.coaching.producer.model.request.CoachingExamDataRequest;
import com.paytm.digital.education.enums.ExamType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IngestorCoachingExamTransformer {

    public static CoachingExamDataRequest convert(final CoachingExamForm form) {

        final CoachingExamDataRequest request = CoachingExamDataRequest.builder()
                .coachingExamId(form.getCoachingExamId())
                .instituteId(form.getInstituteId())
                .examType(ExamType.fromString(form.getExamType()))
                .examName(form.getExamName())
                .examDescription(form.getExamDescription())
                .courseIds(IngestorCommonTransformer.convertStringToListOfLong(
                        form.getCourseIds()))
                .streamIds(IngestorCommonTransformer.convertStringToListOfLong(
                        form.getStreamIds()))
                .examDuration(form.getExamDuration())
                .maximumMarks(form.getMaximumMarks())
                .examDate(null)
                .eligibility(form.getEligibility())
                .priority(form.getGlobalPriority())
                .isEnabled(IngestorCommonTransformer.convertStringToBoolean(
                        form.getStatusActive()))
                .build();

        log.info("CoachingExamDataRequest: {}", request);
        return request;
    }

}
