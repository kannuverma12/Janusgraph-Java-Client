package com.paytm.digital.education.explore.response.dto.detail;

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

    default Long getCollegePredictorPid() {
        return null;
    }
}
