package com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConvTaxInfo {

    @NotNull
    @ApiModelProperty(required = true)
    @JsonProperty("totalCGST")
    private Float totalCGST;

    @NotNull
    @ApiModelProperty(required = true)
    @JsonProperty("totalSGST")
    private Float totalSGST;

    @NotNull
    @ApiModelProperty(required = true)
    @JsonProperty("totalIGST")
    private Float totalIGST;

    @NotNull
    @ApiModelProperty(required = true)
    @JsonProperty("totalUTGST")
    private Float totalUTGST;
}
