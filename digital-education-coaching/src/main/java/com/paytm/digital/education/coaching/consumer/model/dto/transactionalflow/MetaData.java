package com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MetaData {

    @Valid
    @NotNull
    @ApiModelProperty(required = true)
    private ConvTaxInfo convTaxInfo;

    @Valid
    @NotNull
    @ApiModelProperty(required = true)
    private TaxInfo taxInfo;

    @Valid
    @NotNull
    @ApiModelProperty(required = true)
    private TCS tcs;

    private String courseType;

    @NotNull
    @ApiModelProperty(required = true)
    private Long courseId;
    private Long userId;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String merchantProductId;
}
