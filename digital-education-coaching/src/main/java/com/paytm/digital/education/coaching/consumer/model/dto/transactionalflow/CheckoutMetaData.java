package com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CheckoutMetaData {

    @Valid
    @NotNull
    @ApiModelProperty(required = true)
    private ConvTaxInfo convTaxInfo;

    @Valid
    @NotNull
    @ApiModelProperty(required = true)
    private TaxInfo taxInfo;

    private String courseType;

    @NotNull
    private Long courseId;

    private Long userId;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String merchantProductId;
}
