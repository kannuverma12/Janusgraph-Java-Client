package com.paytm.digital.education.coaching.ingestion.model.googleform;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.coaching.ingestion.model.GoogleSheetColumnName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoachingCTAMappingForm {

    @JsonProperty("course_id")
    @GoogleSheetColumnName("Course Id")
    private Long courseId;

    @JsonProperty("cta_id")
    @GoogleSheetColumnName("CTA Id")
    private Long ctaId;

    @JsonProperty("view_type")
    @GoogleSheetColumnName("View Type")
    private String viewType;

}
