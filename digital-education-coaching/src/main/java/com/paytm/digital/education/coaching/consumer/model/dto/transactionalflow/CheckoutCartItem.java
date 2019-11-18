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
public class CheckoutCartItem {

    @NotNull
    @ApiModelProperty(required = true)
    private Long productId;

    @NotNull
    @ApiModelProperty(required = true)
    private Integer quantity;

    @NotNull
    @ApiModelProperty(required = true)
    private Float basePrice;

    @NotNull
    @ApiModelProperty(required = true)
    private Float convFee;

    @NotNull
    @ApiModelProperty(required = true)
    private Float sellingPrice;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String categoryId;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String educationVertical;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String referenceId;

    @NotNull
    @Valid
    @ApiModelProperty(required = true)
    private CheckoutMetaData metaData;
}
