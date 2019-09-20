package com.paytm.digital.education.explore.response.dto.search;

import static com.paytm.digital.education.explore.constants.ExploreConstants.INSTITUTE_SEARCH_CTA;
import static com.paytm.digital.education.explore.constants.ExploreConstants.SCHOOL_SEARCH_CTA;
import static com.paytm.digital.education.explore.enums.EducationEntity.INSTITUTE;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.response.dto.common.CTA;
import com.paytm.digital.education.explore.response.dto.common.OfficialAddress;
import com.paytm.digital.education.explore.response.dto.detail.CTAInfoHolder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InstituteData extends SearchBaseData implements CTAInfoHolder {

    @JsonProperty("institute_id")
    private long instituteId;

    @JsonProperty("official_name")
    private String officialName;

    @JsonProperty("exams")
    private List<String> exams;

    @JsonProperty("approvals")
    private Map<String, String> approvals;

    @JsonProperty("official_address")
    private OfficialAddress officialAddress;

    @JsonProperty("url_display_key")
    private String urlDisplayName;

    @JsonProperty("is_client")
    private boolean client;

    @JsonProperty("brochure_url")
    private String brochureUrl;

    @JsonProperty("cta_list")
    private List<CTA> ctaList;

    @JsonIgnore
    private Long pid;

    @JsonIgnore
    private Long mid;

    @JsonIgnore
    @Accessors(fluent = true)
    private boolean shouldHaveLeadCTA = true;

    @JsonIgnore
    @Accessors(fluent = true)
    private boolean shouldHaveApplyNowCTA = false;

    @Override
    @JsonIgnore
    public String getBrochureUrl() {
        return brochureUrl;
    }

    @JsonIgnore
    @Override
    public EducationEntity getCorrespondingEntity() {
        return INSTITUTE;
    }

    @JsonIgnore
    @Accessors(fluent = true)
    private String ctaDbPropertyKey = INSTITUTE_SEARCH_CTA;
}
