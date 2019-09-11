package com.paytm.digital.education.coaching.ingestion.transformer;

import com.paytm.digital.education.coaching.ingestion.model.googleform.CompetitiveExamForm;
import com.paytm.digital.education.coaching.producer.model.request.TargetExamUpdateRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IngestorCompetitiveExamTransformer {

    public static TargetExamUpdateRequest convert(final CompetitiveExamForm form) {
        if (null == form) {
            return null;
        }
        return TargetExamUpdateRequest.builder()
                .examId(form.getExamId())
                .streamIds(IngestorCommonTransformer.convertStringToListOfLong(
                        form.getDomains()))
                .priority(form.getGlobalPriority())
                .build();
    }
}

