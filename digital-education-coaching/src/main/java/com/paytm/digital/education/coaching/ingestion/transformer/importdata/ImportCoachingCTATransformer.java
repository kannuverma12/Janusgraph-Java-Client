package com.paytm.digital.education.coaching.ingestion.transformer.importdata;

import com.paytm.digital.education.coaching.enums.CtaType;
import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingCTAForm;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCtaDataRequest;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;

public class ImportCoachingCTATransformer {

    private static final Logger log = LoggerFactory.getLogger(ImportCoachingCTATransformer.class);

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
                .properties(
                        ImportCommonTransformer.convertStringToListOfString(form.getProperties()))
                .build();
    }
}
