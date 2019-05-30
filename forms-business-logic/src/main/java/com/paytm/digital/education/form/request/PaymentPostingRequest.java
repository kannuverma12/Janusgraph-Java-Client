package com.paytm.digital.education.form.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentPostingRequest {

    @JsonProperty("id")
    private String id;

    @JsonProperty("customer_id")
    @NotNull
    private Long customerId;

    @JsonProperty("payment_status")
    private Integer paymentStatus;

    @JsonProperty("created_at")
    private Date createdAt;

    @JsonProperty("updated_at")
    private Date updatedAt;

    @JsonProperty("customer_email")
    private String customerEmail;

    @JsonProperty("customer_firstname")
    private String customerFirstname;

    @JsonProperty("customer_lastname")
    private String customerLastname;

    @JsonProperty("phone")
    private String phoneNumber;

    @JsonProperty("remote_ip")
    private String remoteIp;

    @JsonProperty("title")
    private String title;

    @JsonProperty("items")
    @NotNull
    private List<PaymentPostingItemRequest> items;
}
