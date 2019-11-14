package com.paytm.digital.education.coaching.producer.model.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.coaching.producer.model.embedded.ImportantDate;
import com.paytm.digital.education.enums.CTAViewType;
import com.paytm.digital.education.enums.CourseCover;
import com.paytm.digital.education.enums.CourseLevel;
import com.paytm.digital.education.enums.CourseType;
import com.paytm.digital.education.enums.DurationType;
import com.paytm.digital.education.enums.Language;
import com.paytm.digital.education.validator.PositiveElementsCollection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.List;
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
