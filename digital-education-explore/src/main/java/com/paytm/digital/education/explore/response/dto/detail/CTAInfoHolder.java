package com.paytm.digital.education.explore.response.dto.detail;

import static com.paytm.digital.education.explore.constants.ExploreConstants.CTA;

import com.paytm.digital.education.explore.enums.EducationEntity;

public interface CTAInfoHolder {
    Long getPid();

    boolean shouldHaveLeadCTA();

    boolean shouldHaveApplyNowCTA();

    boolean isClient();

    String getBrochureUrl();

    EducationEntity getCorrespondingEntity();

    default String getFormId() {
        return null;
    }

    default boolean hasCompareFeature() {
        return true;
    }

    default boolean hasShortListFeature() {
        return true;
    }

    default boolean hasShareFeature() {
        return false;
    }

    default Long getCollegePredictorPid() {
        return null;
    }

    default String ctaDbPropertyKey() {
        return CTA;
    }
}
