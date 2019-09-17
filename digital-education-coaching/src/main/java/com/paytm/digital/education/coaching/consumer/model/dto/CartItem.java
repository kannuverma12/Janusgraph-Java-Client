package com.paytm.digital.education.coaching.consumer.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CartItem {

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

    private String categoryId;

    @NotEmpty
    private String educationVertical;

    @NotEmpty
    private String referenceId;

    @Valid
    private MetaData metaData;
}
