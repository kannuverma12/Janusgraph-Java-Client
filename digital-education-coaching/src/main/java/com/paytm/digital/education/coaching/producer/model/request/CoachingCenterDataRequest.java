package com.paytm.digital.education.coaching.producer.model.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.coaching.producer.model.embedded.OfficialAddress;
import com.paytm.digital.education.enums.CourseType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Validated
public class CoachingCenterDataRequest {

    @Min(value = 1)
    @ApiModelProperty(value = "unique existing id of the coaching center, ignore for new record")
    private Long centerId;

    @NotNull
    @Min(value = 1)
    @ApiModelProperty(value = "id of the coaching institute")
    private Long instituteId;

    @NotEmpty
    @Size(max = 20)
    @ApiModelProperty(value = "name of the center")
    private String officialName;

    @Valid
    @NotNull
    private OfficialAddress officialAddress;

    @NotEmpty
    @ApiModelProperty(value = "elements from predefined course types")
    private List<CourseType> courseTypes;

    @NotNull
    @Min(value = 1)
    @ApiModelProperty(value = "priority of coaching center")
    private Integer priority;

    @ApiModelProperty(value = "flag to enable/disable the center")
    private Boolean isEnabled = Boolean.TRUE;
}
