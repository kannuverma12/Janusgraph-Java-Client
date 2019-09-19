package com.paytm.digital.education.coaching.ingestion.model.googleform;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.coaching.ingestion.model.GoogleSheetColumnName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoachingCourseFeatureForm {

    @JsonProperty("course_facility_id")
    @GoogleSheetColumnName("Course Facility Id")
    private Long courseFacilityId;

    @JsonProperty("institute_id")
    @GoogleSheetColumnName("Institute Id")
    private Long instituteId;

    @JsonProperty("facility_type")
    @GoogleSheetColumnName("Facility Type")
    private String facilityType;

    @JsonProperty("logo")
    @GoogleSheetColumnName("Logo")
    private String logo;

    @JsonProperty("facility_description")
    @GoogleSheetColumnName("Facility Description")
    private String facilityDescription;

    @JsonProperty("priority")
    @GoogleSheetColumnName("Priority")
    private Integer priority;

    @JsonProperty("status_active")
    @GoogleSheetColumnName("Status Active")
    private String statusActive;
}
