package com.paytm.digital.education.form.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LatestFormData {

    @JsonProperty("last_saved_data")
    FormData lastSavedData;

    @JsonProperty("last_order_data")
    FormData lastOrderData;
}
