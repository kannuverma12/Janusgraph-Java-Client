package com.paytm.digital.education.ingestion.request;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Validated
public class StreamDataRequest {

    @Min(value = 1)
    @ApiModelProperty(value = "unique existing stream id, should be ignored in case of new record")
    private Long streamId;

    @NotEmpty
    @Size(max = 50)
    @Pattern(regexp = "^[a-zA-Z]+[a-zA-Z& ]*$")
    @ApiModelProperty(value = "name of the stream")
    private String name;


    @NotEmpty
    @Size(max = 50)
    @Pattern(regexp = "^[a-zA-Z]+[a-zA-Z& ]*$")
    @ApiModelProperty(value = "short name of the stream")
    private String shortName;

    @NotNull
    @Min(value = 1)
    @ApiModelProperty(value = "stream ranking across all the existing streams")
    private Integer priority;

    @ApiModelProperty(value = "stream logo url")
    private String logo;

    @ApiModelProperty(value = "flag to enable/disable the stream")
    private Boolean isEnabled = Boolean.TRUE;

}
