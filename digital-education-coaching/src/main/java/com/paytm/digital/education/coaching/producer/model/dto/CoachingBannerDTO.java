package com.paytm.digital.education.coaching.producer.model.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CoachingBannerDTO {

    private Long coachingBannerId;

    private String bannerImageUrl;

    private String redirectionUrl;

    private Integer priority;

    private Boolean isEnable;
}
