package com.paytm.digital.education.coaching.consumer.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FetchCartItemsRequestBody {

    @NotEmpty
    @ApiModelProperty(required = true)
    private String transactionId;

    @NotEmpty
    @ApiModelProperty(required = true)
    private String merchantData;

    @NotNull
    @ApiModelProperty(required = true)
    private Long userId;

    @NotNull
    @ApiModelProperty(required = true)
    private Long merchantId;

}
