package com.paytm.digital.education.coaching.ingestion.transformer.importdata;

import com.google.common.collect.ImmutableMap;
import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingCTAMappingForm;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCoursePatchRequest;
import com.paytm.digital.education.enums.CTAViewType;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class ImportCoachingCTAMappingTransformer {

    private static final Logger log =
            LoggerFactory.getLogger(ImportCoachingCTAMappingTransformer.class);

    public static CoachingCoursePatchRequest convert(final CoachingCTAMappingForm form) {
        if (null == form) {
            return null;
        }

        Map<CTAViewType, Long> ctaInfo = null;
        CTAViewType ctaViewType = CTAViewType.fromString(form.getViewType());
        if (ctaViewType != null && form.getCtaId() != null) {
            ctaInfo = ImmutableMap.of(CTAViewType.fromString(form.getViewType()), form.getCtaId());
        }
        return CoachingCoursePatchRequest
                .builder()
                .courseId(form.getCourseId())
                .ctaInfo(Optional.ofNullable(ctaInfo).orElse(ImmutableMap.of()))
                .build();
    }
}
