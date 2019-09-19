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
public class CoachingBannerForm {

    @JsonProperty("id")
    @GoogleSheetColumnName("Id")
    private Long id;

    @JsonProperty("banner_image_url")
    @GoogleSheetColumnName("Banner Image Url")
    private String bannerImageUrl;

    @JsonProperty("redirection_url")
    @GoogleSheetColumnName("Redirection Url")
    private String redirectionUrl;

    @JsonProperty("global_priority")
    @GoogleSheetColumnName("Global Priority")
    private Integer globalPriority;

    @JsonProperty("status_active")
    @GoogleSheetColumnName("Status Active")
    private String statusActive;
}

