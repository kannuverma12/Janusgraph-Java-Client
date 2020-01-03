package com.paytm.digital.education.coaching.producer.model.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.enums.CTAViewType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Validated
public class CoachingCoursePatchRequest {

    @Min(value = 1)
    @ApiModelProperty(value = "unique existing course id, should be ignored in case of new record")
    private Long courseId;

    private Map<CTAViewType, Long> ctaInfo;
}
