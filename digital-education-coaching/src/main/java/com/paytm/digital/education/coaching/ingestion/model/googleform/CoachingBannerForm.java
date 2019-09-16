package com.paytm.digital.education.coaching.ingestion.model.googleform;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CoachingBannerForm {

    private Long   id;
    private String bannerImageUrl;
    private String redirectionUrl;

    private Integer globalPriority;
    private String  statusActive;
}

