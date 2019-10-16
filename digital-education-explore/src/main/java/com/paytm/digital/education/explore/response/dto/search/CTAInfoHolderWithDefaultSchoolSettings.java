package com.paytm.digital.education.explore.response.dto.search;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.response.dto.detail.CTAInfoHolder;

import static com.paytm.digital.education.explore.enums.EducationEntity.SCHOOL;

public interface CTAInfoHolderWithDefaultSchoolSettings extends CTAInfoHolder {
    @Override
    default boolean shouldHaveLeadCTA() {
        return false;
    }

    @Override
    default boolean shouldHaveApplyNowCTA() {
        return true;
    }

    @Override
    @JsonIgnore
    default boolean isClient() {
        return false;
    }

    @Override
    @JsonIgnore
    default EducationEntity getCorrespondingEntity() {
        return SCHOOL;
    }

    @Override
    default boolean hasCompareFeature() {
        return false;
    }

    @Override
    default boolean hasShareFeature() {
        return false;
    }
}
