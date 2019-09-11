package com.paytm.digital.education.coaching.producer.model.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CoachingBannerDataRequest {

    @Min(value = 1)
    private Long coachingBannerId;

    @URL
    @NotNull
    private String bannerImageUrl;

    @URL
    private String redirectionUrl;

    @NotNull
    @Min(value = 1)
    private Integer priority;

    @NotNull
    private Boolean isEnabled = Boolean.TRUE;
}
