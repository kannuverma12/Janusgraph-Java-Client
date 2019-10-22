package com.paytm.digital.education.explore.response.dto.detail.school.detail;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.database.entity.SchoolFeeDetails;
import com.paytm.digital.education.explore.response.dto.common.BannerData;
import com.paytm.digital.education.explore.response.dto.common.CTA;
import com.paytm.digital.education.explore.response.dto.detail.Attribute;
import com.paytm.digital.education.explore.response.dto.search.CTAInfoHolderWithDefaultSchoolSettings;
import com.paytm.digital.education.explore.response.dto.search.SchoolSearchData;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class SchoolDetail implements CTAInfoHolderWithDefaultSchoolSettings {
    @JsonProperty("school_id")
    private Long schoolId;

    @JsonProperty("shifts")
    private List<ShiftTable> shiftTables;

    @JsonProperty("faculty")
    private FacultyDetail facultyDetail;

    @JsonProperty("fees_data")
    private List<SchoolFeeDetails> feesDetails;

    @JsonProperty("facilities")
    private List<FacilityResponse> facilities;

    @JsonProperty("important_dates")
    private List<ImportantDate> importantDateSections;

    @JsonProperty("gallery")
    private SchoolGalleryResponse gallery;

    @JsonProperty("derived_attributes")
    private Map<String, List<Attribute>> derivedAttributes;

    @JsonProperty("class_info")
    private List<ClassLevelTable> classInfoTable;

    @JsonProperty("streams")
    private List<String> streams;

    @JsonProperty("general_information")
    private GeneralInformation generalInformation;

    @JsonProperty("cta_list")
    private List<CTA> ctaList;

    @JsonProperty("nearby_schools")
    private List<SchoolSearchData> nearbySchools;

    @JsonIgnore
    private Long pid;

    @JsonIgnore
    private String formId;

    @JsonIgnore
    private String brochureUrl;

    @JsonProperty("shortlisted")
    private boolean shortlisted;

    @JsonProperty("banners")
    private List<BannerData> banners;
}
