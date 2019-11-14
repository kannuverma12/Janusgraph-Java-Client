package com.paytm.digital.education.coaching.ingestion.model.googleform;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.coaching.ingestion.model.GoogleSheetColumnName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoachingCTAForm {

    @JsonProperty("cta_id")
    @GoogleSheetColumnName("CTA Id")
    private Long ctaId;

    @JsonProperty("name")
    @GoogleSheetColumnName("Name")
    private String name;

    @JsonProperty("description")
    @GoogleSheetColumnName("Description")
    private String description;

    @JsonProperty("cta_type")
    @GoogleSheetColumnName("CTA Type")
    private String ctaType;

    @JsonProperty("logo_url")
    @GoogleSheetColumnName("Logo Url")
    private String logoUrl;

    @JsonProperty("cta_url")
    @GoogleSheetColumnName("CTA Url")
    private String ctaUrl;

    @JsonProperty("properties")
    @GoogleSheetColumnName("Properties")
    private String properties;
}
