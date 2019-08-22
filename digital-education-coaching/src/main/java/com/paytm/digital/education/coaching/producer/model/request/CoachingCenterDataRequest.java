package com.paytm.digital.education.coaching.producer.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.database.embedded.OfficialAddress;
import com.paytm.digital.education.enums.CourseType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoachingCenterDataRequest {

    @JsonProperty("center_id")
    @ApiModelProperty(value = "unique existing id of the coaching center, ignore for new record")
    private Long centerId;

    @NotNull
    @JsonProperty("institute_id")
    @ApiModelProperty(value = "id of the coaching institute")
    private Long instituteId;

    @NotEmpty
    @JsonProperty("official_name")
    @ApiModelProperty(value = "name of the center")
    private String officialName;

    @Valid
    @NotNull
    @JsonProperty("official_address")
    private OfficialAddress officialAddress;

    @NotNull
    @JsonProperty("course_types")
    private List<CourseType> courseTypes;

    @NotNull
    @Min(value = 1)
    @ApiModelProperty(value = "coaching center ranking across all the existing centres")
    private Integer priority;

    @NotNull
    @JsonProperty("is_enabled")
    @ApiModelProperty(value = "flag to enable/disable the center")
    private Boolean isEnabled = Boolean.TRUE;
}
