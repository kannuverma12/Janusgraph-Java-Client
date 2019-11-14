package com.paytm.digital.education.coaching.ingestion.transformer.importdata;

import com.paytm.digital.education.coaching.enums.CtaType;
import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingBannerForm;
import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingCTAForm;
import com.paytm.digital.education.coaching.producer.model.request.CoachingBannerDataRequest;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCtaDataRequest;
import com.paytm.digital.education.enums.CTAType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImportCoachingCTATransformer {

    public static CoachingCtaDataRequest convert(final CoachingCTAForm form) {
        if (null == form) {
            return null;
        }
        return CoachingCtaDataRequest.builder()
                .ctaId(form.getCtaId())
                .name(form.getName())
                .description(form.getDescription())
                .ctaType(CtaType.valueOf(form.getCtaType()))
                .logoUrl(form.getLogoUrl())
                .url(form.getCtaUrl())
                .properties(ImportCommonTransformer.convertStringToListOfString(form.getProperties()))
                .build();
    }
}
