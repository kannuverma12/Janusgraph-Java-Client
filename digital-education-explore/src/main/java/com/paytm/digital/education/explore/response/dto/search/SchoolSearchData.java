package com.paytm.digital.education.explore.response.dto.search;

import static com.paytm.digital.education.explore.constants.ExploreConstants.SCHOOL_SEARCH_CTA;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.response.dto.common.OfficialAddress;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SchoolSearchData extends SearchBaseData implements CTAInfoHolderWithDefaultSchoolSettings {

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

    @Override
    public boolean hasShareFeature() {
        return false;
    }

    @JsonProperty("distance")
    private String distance;

    @JsonIgnore
    @Accessors(fluent = true)
    private String ctaDbPropertyKey = SCHOOL_SEARCH_CTA;

}
