package com.paytm.digital.education.explore.response.dto.detail.school.detail;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.database.entity.SchoolFeeDetails;
import com.paytm.digital.education.explore.database.entity.ShiftDetails;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.response.dto.common.CTA;
import com.paytm.digital.education.explore.response.dto.detail.Attribute;
import com.paytm.digital.education.explore.response.dto.detail.CTAInfoHolder;
import com.paytm.digital.education.explore.response.dto.detail.ClassInfoLegend;
import com.paytm.digital.education.explore.response.dto.search.SchoolSearchData;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

import static com.paytm.digital.education.explore.enums.EducationEntity.SCHOOL;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class SchoolDetail implements CTAInfoHolder {
    @JsonProperty("school_id")
    private Long schoolId;

    @JsonProperty("shifts")
    private List<ShiftDetails> shiftDetailsList;

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
    private List<ClassInfoLegend> classInfoLegend = ClassInfoLegend.CLASS_INFO_STATIC_CONTENT_LIST;

    @JsonProperty("streams")
    private List<String> streams;

    @JsonProperty("general_information")
    private GeneralInformation generalInformation;

    @JsonProperty("cta_list")
    private List<CTA> ctaList;

    @JsonProperty("similar_schools")
    private List<SchoolSearchData> similarSchools;

    @JsonIgnore
    private Long pid;

    @JsonIgnore
    private String formId;

    @JsonIgnore
    private String brochureUrl;

    @JsonIgnore
    @Accessors(fluent = true)
    private boolean shouldHaveLeadCTA = false;

    @JsonIgnore
    @Accessors(fluent = true)
    private boolean shouldHaveApplyNowCTA = true;

    @JsonProperty("shortlisted")
    private boolean shortlisted;

    @Override
    @JsonIgnore
    public boolean isClient() {
        return false;
    }

    @Override
    @JsonIgnore
    public EducationEntity getCorrespondingEntity() {
        return SCHOOL;
    }

    @Override
    @JsonIgnore
    public boolean hasCompareFeature() {
        return false;
    }

    @Override public Long getCollegePredictorPid() {
        return null;
    }
}
