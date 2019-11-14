package com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
    private Long productId;

    @NotNull
    private Integer quantity;

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

    @NotNull
    @Valid
    private CheckoutMetaData metaData;
}
