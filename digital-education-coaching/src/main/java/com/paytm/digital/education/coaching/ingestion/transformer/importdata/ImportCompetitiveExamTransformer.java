package com.paytm.digital.education.coaching.ingestion.transformer.importdata;

import com.paytm.digital.education.coaching.ingestion.model.googleform.CompetitiveExamForm;
import com.paytm.digital.education.coaching.producer.model.request.TargetExamUpdateRequest;

public class ImportCompetitiveExamTransformer {

    public static TargetExamUpdateRequest convert(final CompetitiveExamForm form) {
        if (null == form) {
            return null;
        }
        return TargetExamUpdateRequest.builder()
                .examId(form.getExamId())
                .streamIds(ImportCommonTransformer.convertStringToListOfLong(
                        form.getDomains()))
                .priority(form.getGlobalPriority())
                .build();
    }
}

