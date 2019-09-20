package com.paytm.digital.education.explore.response.dto.search;

import static com.paytm.digital.education.explore.constants.ExploreConstants.SCHOOL_SEARCH_CTA;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.response.dto.common.OfficialAddress;
import com.paytm.digital.education.explore.response.dto.detail.CTAInfoHolder;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SchoolSearchData extends SearchBaseData implements CTAInfoHolder {

    @JsonProperty("school_id")
    private long schoolId;

    @JsonProperty("official_name")
    private String officialName;

    @JsonProperty("official_address")
    private OfficialAddress officialAddress;

    @JsonProperty("url_display_key")
    private String urlDisplayName;

    private boolean isClient;

    @JsonIgnore
    private Long pid;

    @JsonIgnore
    private String formId;

    @JsonIgnore
    private String brochureUrl;

    public Long getPid() {
        return null;
    }

    @JsonIgnore
    @Accessors(fluent = true)
    private boolean shouldHaveLeadCTA = false;

    @JsonIgnore
    @Accessors(fluent = true)
    private boolean shouldHaveApplyNowCTA = true;

    @JsonIgnore
    @Override
    public boolean isClient() {
        return false;
    }

    @JsonIgnore
    @Override
    public String getBrochureUrl() {
        return brochureUrl;
    }

    @JsonIgnore
    @Override
    public EducationEntity getCorrespondingEntity() {
        return EducationEntity.SCHOOL;
    }

    @JsonIgnore
    @Override
    public boolean hasCompareFeature() {
        return false;
    }

    @JsonProperty("distance")
    private String distance;

    @JsonIgnore
    @Accessors(fluent = true)
    private String ctaDbPropertyKey = SCHOOL_SEARCH_CTA;

}
