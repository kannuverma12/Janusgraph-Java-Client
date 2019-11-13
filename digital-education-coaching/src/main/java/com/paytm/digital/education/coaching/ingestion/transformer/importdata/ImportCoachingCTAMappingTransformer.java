package com.paytm.digital.education.coaching.ingestion.transformer.importdata;

import com.google.common.collect.ImmutableMap;
import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingBannerForm;
import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingCTAMappingForm;
import com.paytm.digital.education.coaching.producer.model.request.CoachingBannerDataRequest;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCoursePatchRequest;
import com.paytm.digital.education.enums.CTAViewType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImportCoachingCTAMappingTransformer {

    public static CoachingCoursePatchRequest convert(final CoachingCTAMappingForm form) {
        if (null == form) {
            return null;
        }
        return CoachingCoursePatchRequest
                .builder()
                .courseId(form.getCourseId())
                .ctaInfo(ImmutableMap.of(CTAViewType.fromString(form.getViewType()), form.getCtaId()))
                .build();
    }
}
