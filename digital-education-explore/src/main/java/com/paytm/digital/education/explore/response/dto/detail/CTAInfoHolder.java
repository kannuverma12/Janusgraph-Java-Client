package com.paytm.digital.education.explore.response.dto.detail;

import com.paytm.digital.education.explore.enums.EducationEntity;

public interface CTAInfoHolder {
    Long getPid();

    boolean shouldHaveLeadCTA();

    boolean shouldHaveApplyNowCTA();

    boolean isClient();

    String getBrochureUrl();

    EducationEntity getCorrespondingEntity();

    String getFormId();

    default boolean hasCompareFeature() {
        return true;
    }
}
