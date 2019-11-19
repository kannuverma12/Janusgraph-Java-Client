package com.paytm.digital.education.coaching.ingestion.transformer.importdata;

import com.google.common.collect.ImmutableMap;
import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingCTAMappingForm;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCoursePatchRequest;
import com.paytm.digital.education.enums.CTAViewType;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;

import java.util.Optional;

public class ImportCoachingCTAMappingTransformer {

    private static final Logger log =
            LoggerFactory.getLogger(ImportCoachingCTAMappingTransformer.class);

    public static CoachingCoursePatchRequest convert(final CoachingCTAMappingForm form) {
        if (null == form) {
            return null;
        }
        return CoachingCoursePatchRequest
                .builder()
                .courseId(form.getCourseId())
                .ctaInfo(Optional.ofNullable(CTAViewType.fromString(form.getViewType()))
                        .map(ctaViewType -> ImmutableMap.of(ctaViewType, form.getCtaId()))
                        .orElse(ImmutableMap.of()))
                .build();
    }
}
