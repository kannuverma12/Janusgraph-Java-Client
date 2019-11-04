package com.paytm.digital.education.coaching.producer.model.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.coaching.enums.CoachingCourseFeatureName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Validated
public class CoachingCourseFeatureDataRequest {

    @Min(value = 1)
    private Long coachingCourseFeatureId;

    @NotNull
    @Min(value = 1)
    private Long instituteId;

    private String logo;

    @Size(min = 1, max = 200)
    @NotBlank
    private String description;

    @NotNull
    @Min(value = 1)
    private Integer priority;

    @NotNull
    private CoachingCourseFeatureName coachingCourseFeatureName;

    @NotNull
    private Boolean isEnabled = Boolean.TRUE;

}
