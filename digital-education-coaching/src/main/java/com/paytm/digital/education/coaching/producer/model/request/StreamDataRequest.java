package com.paytm.digital.education.coaching.producer.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@ApiModel
public class StreamDataRequest {

    @Min(value = 1)
    @ApiModelProperty(value = "unique existing stream id, should be ignored in case of new record")
    private Long streamId;

    @NotEmpty
    @Size(max = 50)
    @ApiModelProperty(value = "name of the stream")
    private String name;

    @NotNull
    @Min(value = 1)
    @ApiModelProperty(value = "stream ranking across all the existing streams")
    private Integer priority;

    @URL
    @ApiModelProperty(value = "stream logo url")
    @NotNull
    private String logo;

    @NotNull
    @JsonProperty("is_enabled")
    @ApiModelProperty(value = "flag to enable/disable the stream")
    private Boolean isEnabled = Boolean.TRUE;

    @NotEmpty
    @JsonProperty("top_institutes")
    @ApiModelProperty(value = "top institutes id for this streams")
    private List<Long> topInstitutes;

}
