package com.paytm.digital.education.coaching.producer.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class StreamCreateRequest {

    @NotNull
    @JsonProperty("id")
    @ApiModelProperty(value = "id of the stream\n")
    public Long id;

    @NotNull
    @JsonProperty("name")
    @ApiModelProperty(value = "name of the stream\n")
    public String name;

    @NotNull
    @JsonProperty("banner_properties")
    @ApiModelProperty(
            value = "json data which governs the display pattern",
            example = "{\"bg_color\": \"aaaa\", \"text\" : \"management\"}\n")
    public String bannerProperties;

    @NotNull
    @JsonProperty("description")
    @ApiModelProperty(value = "description of the stream\n", notes = "getting sheet data")
    public String description;

    @NotNull
    @JsonProperty("priority")
    public Integer priority;

    @NotNull
    @JsonProperty("is_enabled")
    public Boolean isEnabled = Boolean.TRUE;
}
