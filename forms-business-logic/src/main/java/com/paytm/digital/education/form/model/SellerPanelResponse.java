package com.paytm.digital.education.form.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class SellerPanelResponse {

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("date")
    private Date date;

    @JsonProperty("amount")
    private Float amount;

    @JsonProperty("status")
    private FormStatus status;

    public SellerPanelResponse(FormData formData) {
        orderId = formData.getFormFulfilment().getOrderId().toString();
        date = formData.getFormFulfilment().getCreatedDate();
        amount = formData.getCandidateDetails().getAmount();
        status = formData.getStatus();
    }
}
