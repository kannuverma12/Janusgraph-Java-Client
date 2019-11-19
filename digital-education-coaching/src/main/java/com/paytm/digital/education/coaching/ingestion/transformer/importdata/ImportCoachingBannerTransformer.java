package com.paytm.digital.education.coaching.ingestion.transformer.importdata;

import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingBannerForm;
import com.paytm.digital.education.coaching.producer.model.request.CoachingBannerDataRequest;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;

public class ImportCoachingBannerTransformer {

    private static final Logger log =
            LoggerFactory.getLogger(ImportCoachingBannerTransformer.class);

    public static CoachingBannerDataRequest convert(final CoachingBannerForm form) {
        if (null == form) {
            return null;
        }
        return CoachingBannerDataRequest.builder()
                .coachingBannerId(form.getId())
                .bannerImageUrl(form.getBannerImageUrl())
                .redirectionUrl(form.getRedirectionUrl())
                .priority(form.getGlobalPriority())
                .isEnabled(ImportCommonTransformer.convertStringToBoolean(
                        form.getStatusActive()))
                .build();
    }
}
