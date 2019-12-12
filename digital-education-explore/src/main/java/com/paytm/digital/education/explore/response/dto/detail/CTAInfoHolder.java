package com.paytm.digital.education.explore.response.dto.detail;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paytm.digital.education.enums.EducationEntity;

import java.util.HashMap;
import java.util.Map;

import static com.paytm.digital.education.constant.ExploreConstants.CTA;

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
        return false;
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

    @JsonIgnore
    default Map<String, Object> getAdditionalProperties() {
        return new HashMap<>();
    }
}
