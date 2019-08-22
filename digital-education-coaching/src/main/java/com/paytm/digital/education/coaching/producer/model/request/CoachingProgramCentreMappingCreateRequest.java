package com.paytm.digital.education.coaching.producer.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel
public class CoachingProgramCentreMappingCreateRequest {

    @NotNull
    @ApiModelProperty(value = "id of the stream\n")
    public Long id;

    @NotNull
    @ApiModelProperty(value = "id of the stream\n")
    public Long coachingInstituteId;

    @NotNull
    @ApiModelProperty(value = "id of the stream\n")
    public Long coachingProgramId;

    @NotNull
    @ApiModelProperty(value = "id of the stream\n")
    public Long coachingCentreId;

    @NotNull public Integer priority;

    public List<String> importantDates;

    @NotNull
    @JsonProperty("is_enabled")
    public Boolean isEnabled = Boolean.TRUE;
}
