package com.paytm.digital.education.coaching.consumer.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.coaching.consumer.model.dto.transactionalflow.CartItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VerifyRequest {

    @NotEmpty
    @Valid
    @ApiModelProperty(required = true)
    @JsonProperty("cart_items")
    private List<CartItem> cartItems;
}
