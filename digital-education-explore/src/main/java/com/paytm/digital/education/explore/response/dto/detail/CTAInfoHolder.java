package com.paytm.digital.education.explore.response.dto.detail;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paytm.digital.education.database.entity.CTAConfig;
import com.paytm.digital.education.enums.EducationEntity;

import java.util.HashMap;
import java.util.Map;

import static com.paytm.digital.education.constant.ExploreConstants.CTA;
import static com.paytm.digital.education.enums.CTAEntity.EXAM;
import static com.paytm.digital.education.enums.CTAEntity.INSTITUTE;
import static com.paytm.digital.education.enums.CTAEntity.SCHOOL;

public interface CTAInfoHolder {
    Long getPid();

    boolean shouldHaveLeadCTA();

    boolean shouldHaveApplyNowCTA();

    boolean isClient();

    String getBrochureUrl();

    EducationEntity getCorrespondingEntity();

    CTAConfig getCTAConfig(CTAConfigFetchService ctaConfigFetchService);

    default CTAConfig getEntityLevelCTAConfig(CTAConfigFetchService ctaConfigFetchService) {
        EducationEntity educationEntity = getCorrespondingEntity();
        switch (educationEntity) {
            case INSTITUTE:
                return ctaConfigFetchService.fetchCTAConfig(INSTITUTE);
            case EXAM:
                return ctaConfigFetchService.fetchCTAConfig(EXAM);
            case SCHOOL:
                return ctaConfigFetchService.fetchCTAConfig(SCHOOL);
            default:
                return null;
        }
    }

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

    @JsonIgnore
    default Map<String, Object> getAdditionalProperties() {
        return new HashMap<>();
    }
}
