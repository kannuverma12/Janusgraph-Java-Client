package com.paytm.digital.education.coaching.consumer.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.coaching.consumer.model.dto.CheckoutCartItem;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckoutDataRequest {

    @NotNull
    @JsonProperty("cart_items")
    private List<CheckoutCartItem> cartItems;
}
