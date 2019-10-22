package com.paytm.digital.education.explore.response.dto.search;

import static com.paytm.digital.education.explore.constants.ExploreConstants.EXAM_SEARCH_CTA;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.response.dto.detail.CTAInfoHolder;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExamData extends SearchBaseData implements CTAInfoHolder {

    @JsonProperty("exam_id")
    private int          examId;

    @JsonProperty("url_display_key")
    private String urlDisplayName;

    @JsonProperty("official_name")
    private String       officialName;

    @JsonProperty("application_month")
    private String       applicationMonth;

    @JsonProperty("result_month")
    private String       resultMonth;

    @JsonProperty("exam_month")
    private String       examMonth;

    @JsonProperty("result_start_date")
    private String       resultStartDate;

    @JsonProperty("result_end_date")
    private String       resultEndDate;

    @JsonProperty("application_start_date")
    private String       applicationStartDate;

    @JsonProperty("application_end_date")
    private String       applicationEndDate;

    @JsonProperty("exam_start_date")
    private String       examStartDate;

    @JsonProperty("exam_end_date")
    private String       examEndDate;

    @JsonProperty("logo_url")
    private String       logoUrl;

    @JsonProperty("data_available")
    private List<String> dataAvailable;

    @JsonIgnore
    private Long collegePredictorPid;

    @JsonIgnore
    private String formId;

    public Long getPid() {
        return null;
    }

    @JsonIgnore
    @Accessors(fluent = true)
    private boolean shouldHaveLeadCTA = true;

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
    @Accessors(fluent = true)
    private String ctaDbPropertyKey = EXAM_SEARCH_CTA;

    @Override
    public Long getCollegePredictorPid() {
        return collegePredictorPid;
    }
}
