package com.paytm.digital.education.form.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SellerPanelResponse {

    @JsonProperty("order_id")
    private Long orderId;

    @JsonProperty("date")
    private Date date;

    @JsonProperty("amount")
    private Float amount;

    @JsonProperty("status")
    private String status;

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    public SellerPanelResponse(FormData formData) {
        try {
            orderId = formData.getFormFulfilment().getOrderId();
            date = formData.getFormFulfilment().getCreatedDate();
            if (formData.getCandidateDetails() != null) {
                amount = formData.getCandidateDetails().getAmount();
                email = formData.getCandidateDetails().getEmail();
                name = formData.getCandidateDetails().getFullName();
            }
            if (formData.getFormFulfilment().getPaymentStatus() == null
                    || formData.getFormFulfilment().getPaymentStatus().isEmpty()) {
                status = "PENDING";
            } else {
                status = formData.getFormFulfilment().getPaymentStatus().toUpperCase();
            }

        } catch (Exception e) {
            throw e;
        }
    }
}
