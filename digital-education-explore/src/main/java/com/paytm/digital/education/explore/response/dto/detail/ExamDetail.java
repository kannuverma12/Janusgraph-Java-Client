package com.paytm.digital.education.explore.response.dto.detail;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.dto.detail.Event;
import com.paytm.digital.education.dto.detail.Syllabus;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.explore.response.dto.common.BannerData;
import com.paytm.digital.education.explore.response.dto.common.CTA;
import com.paytm.digital.education.explore.response.dto.common.Widget;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExamDetail implements CTAInfoHolder {

    @JsonProperty("exam_id")
    private Long examId;

    @JsonProperty("exam_full_name")
    private String examFullName;

    @JsonProperty("url_display_key")
    private String urlDisplayName;

    @JsonProperty("interested")
    private boolean interested;

    @JsonProperty("exam_short_name")
    private String examShortName;

    @JsonProperty("about")
    private String about;

    @JsonProperty("exam_level")
    private String examLevel;

    @JsonProperty("logo_url")
    private String logoUrl;

    @JsonProperty("linguistic_medium")
    private List<String> linguisticMedium;

    @JsonProperty("duration_in_hour")
    private Float durationInHour;

    @JsonProperty("centers_count")
    private Integer centersCount;

    @JsonProperty("application_start_date")
    private String applicationOpening;

    @JsonProperty("application_end_date")
    private String applicationClosing;

    @JsonProperty("exam_start_date")
    private String examStartDate;

    @JsonProperty("exam_end_date")
    private String examEndDate;

    @JsonProperty("application_month")
    private String applicationMonth;

    @JsonProperty("exam_month")
    private String examMonth;

    @JsonProperty("eligibility_criteria")
    private String eligibilityCriteria;

    @JsonProperty("syllabus")
    private List<Syllabus> syllabus;

    @JsonProperty("application_fee")
    private Integer applicationFee;                // DNA

    @JsonProperty("important_dates")
    private List<Event> importantDates;

    @JsonProperty("application_process")
    private String applicationProcess;            // DNA

    @JsonProperty("exam_pattern")
    private String examPattern;

    @JsonProperty("admit_card")
    private String admitCard;

    @JsonProperty("answer_key")
    private String answerKey;                     // DNA

    @JsonProperty("result")
    private String result;

    @JsonProperty("cutoff")
    private String cutoff;

    @JsonProperty("counselling")
    private String counselling;                   // DNA

    @JsonProperty("documents_counselling")
    private List<String> documentsRequiredAtCounselling;

    @JsonProperty("documents_exam")
    private List<String> documentsRequiredAtExam;

    @JsonProperty("shortlisted")
    private boolean shortlisted;

    @JsonProperty("derived_attributes")
    private Map<String, List<Attribute>> derivedAttributes;

    @JsonProperty("exam_centers")
    private List<Location> examCenters;

    @JsonProperty("sections")
    private List<String> sections;

    @JsonProperty("widgets")
    private List<Widget> widgets;

    @JsonProperty("banners")
    private List<BannerData> banners;

    @JsonProperty("cta_list")
    private List<CTA> ctaList;

    @JsonProperty("eligibility")
    private String eligibility;

    @JsonProperty("application_form")
    private String applicationForm;

    @JsonProperty("sections_list")
    private List<SectionDataHolder> sectionDataHolders;

    @JsonProperty("terms_and_conditions")
    private String termsAndConditions;

    @JsonProperty("privacy_policies")
    private String privacyPolicies;

    @JsonProperty("disclaimer")
    private String disclaimer;

    @JsonProperty("registration_guidelines")
    private String registrationGuidelines;

    @JsonIgnore
    private Long collegePredictorPid;

    @JsonIgnore
    private String formId;

    @JsonIgnore
    private Map<String, Object> additionalProperties;

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
    public boolean isClient() {
        return false;
    }

    @JsonIgnore
    @Override
    public String getBrochureUrl() {
        return null;
    }

    @JsonIgnore
    @Override
    public EducationEntity getCorrespondingEntity() {
        return EducationEntity.EXAM;
    }

    @JsonIgnore
    @Override
    public boolean hasCompareFeature() {
        return false;
    }

    @JsonIgnore
    @Override
    public boolean hasShortListFeature() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean hasShareFeature() {
        return false;
    }

    @Override
    public Long getCollegePredictorPid() {
        return collegePredictorPid;
    }

    @Override
    @JsonIgnore
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }
}
