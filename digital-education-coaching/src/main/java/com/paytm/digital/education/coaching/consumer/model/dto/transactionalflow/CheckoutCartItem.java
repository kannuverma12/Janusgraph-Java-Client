package com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CheckoutCartItem {

    @NotNull
    private Long productId;

    @NotNull
    private Integer qty;

    @NotNull
    private Float basePrice;

    @NotNull
    private Float convFee;

    @NotNull
    private Float sellingPrice;

    @NotEmpty
    private String categoryId;

    @NotEmpty
    private String educationVertical;

    @NotEmpty
    private String referenceId;

    @Valid
    private CheckoutMetaData metaData;
}
