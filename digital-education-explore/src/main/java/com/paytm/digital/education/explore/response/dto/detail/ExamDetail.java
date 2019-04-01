package com.paytm.digital.education.explore.response.dto.detail;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExamDetail {

    @JsonProperty("exam_id")
    private Long           examId;

    @JsonProperty("exam_full_name")
    private String         examFullName;

    @JsonProperty("exam_short_name")
    private String         examShortName;

    @JsonProperty("about")
    private String         about;

    @JsonProperty("exam_level")
    private String         examLevel;

    @JsonProperty("linguistic_medium")
    private List<String>   linguisticMedium;

    @JsonProperty("duration_in_hour")
    private Float          durationInHour;

    @JsonProperty("centers_count")
    private Integer        centersCount;

    @JsonProperty("application_start_date")
    private String         applicationOpening;

    @JsonProperty("application_end_date")
    private String         applicationClosing;

    @JsonProperty("exam_start_date")
    private String         examStartDate;

    @JsonProperty("exam_end_date")
    private String         examEndDate;

    @JsonProperty("application_month")
    private String         applicationMonth;

    @JsonProperty("exam_month")
    private String         examMonth;

    @JsonProperty("eligibility_criteria")
    private String         eligibilityCriteria;

    @JsonProperty("syllabus")
    private List<Syllabus> syllabus;

    @JsonProperty("application_fee")
    private Integer        applicationFee;                // DNA

    @JsonProperty("important_dates")
    private List<Event>    importantDates;

    @JsonProperty("application_process")
    private String         applicationProcess;            // DNA

    @JsonProperty("exam_pattern")
    private String         examPattern;

    @JsonProperty("admit_card")
    private String         admitCard;                     // DNA

    @JsonProperty("answer_key")
    private String         answerKey;                     // DNA

    @JsonProperty("result")
    private String         result;                        // DNA

    @JsonProperty("cutoff")
    private String         cutoff;                        // DNA

    @JsonProperty("counselling")
    private String         counselling;                   // DNA

    @JsonProperty("documents_counselling")
    private List<String>   documentsRequiredAtCounselling;

    @JsonProperty("documents_exam")
    private List<String>   documentsRequiredAtExam;

    @JsonProperty("shortlisted")
    private boolean        shortlisted;

    @JsonProperty("derived_attributes")
    private Map<String, List<Attribute>> derivedAttributes;

    @JsonProperty("exam_centers")
    private List<Location> examCenters;

}
