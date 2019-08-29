package com.paytm.digital.education.coaching.producer.model.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.coaching.producer.model.embedded.OfficialAddress;
import com.paytm.digital.education.enums.CourseType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@ApiModel
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CoachingCenterDataRequest {

    @Min(value = 1)
    @ApiModelProperty(value = "unique existing id of the coaching center, ignore for new record")
    private Long centerId;

    @NotNull
    @Min(value = 1)
    @ApiModelProperty(value = "id of the coaching institute")
    private Long instituteId;

    @NotEmpty
    @Size(max = 100)
    @ApiModelProperty(value = "name of the center")
    private String officialName;

    @Valid
    @NotNull
    private OfficialAddress officialAddress;

    @NotEmpty
    @ApiModelProperty(value = "elements from predefined course types")
    private List<CourseType> courseTypes;

    @Min(value = 0)
    @ApiModelProperty(value = "priority of coaching center")
    private Integer priority = new Integer(0);

    @ApiModelProperty(value = "flag to enable/disable the center")
    private Boolean isEnabled = Boolean.TRUE;
}
