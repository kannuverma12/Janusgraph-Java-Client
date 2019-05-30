package com.paytm.digital.education.form.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class SellerPanelResponse {

    @JsonProperty("order_id")
    private Long orderId;

    @JsonIgnore
    @JsonProperty("date")
    private Date date;

    @JsonIgnore
    @JsonProperty("amount")
    private Float amount;

    @JsonIgnore
    @JsonProperty("status")
    private FormStatus status;

    public SellerPanelResponse(FormData formData) {
        try {
            orderId = formData.getFormFulfilment().getOrderId();
            date = formData.getFormFulfilment().getCreatedDate();
            amount = formData.getCandidateDetails().getAmount();
            status = formData.getStatus();
        } catch (Exception e) {
            throw e;
        }
    }
}
