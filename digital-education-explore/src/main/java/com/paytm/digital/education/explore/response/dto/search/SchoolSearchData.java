package com.paytm.digital.education.explore.response.dto.search;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.dto.OfficialAddress;
import com.paytm.digital.education.explore.es.model.GeoLocation;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import static com.paytm.digital.education.constant.ExploreConstants.SCHOOL_SEARCH_CTA;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
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

    @JsonProperty("contact_logo")
    private String contactLogoUrl;

    @JsonProperty("location_logo")
    private String locationLogoUrl;

    @JsonProperty("location")
    private GeoLocation geoLocation;

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
