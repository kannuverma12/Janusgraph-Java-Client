package com.paytm.digital.education.coaching.ingestion.transformer;

import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingBannerForm;
import com.paytm.digital.education.coaching.producer.model.request.CoachingBannerDataRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IngestorCoachingBannerTransformer {

    public static CoachingBannerDataRequest convert(final CoachingBannerForm form) {
        if (null == form) {
            return null;
        }
        return CoachingBannerDataRequest.builder()
                .coachingBannerId(form.getId())
                .bannerImageUrl(form.getBannerImageUrl())
                .redirectionUrl(form.getRedirectionUrl())
                .priority(form.getGlobalPriority())
                .isEnabled(IngestorCommonTransformer.convertStringToBoolean(
                        form.getStatusActive()))
                .build();
    }
}
