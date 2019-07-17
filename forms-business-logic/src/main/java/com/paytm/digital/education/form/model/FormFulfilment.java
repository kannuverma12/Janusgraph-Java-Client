package com.paytm.digital.education.form.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FormFulfilment {

    @Field("orderId")
    private Long orderId;

    @Field("itemId")
    private Long itemId;

    @Field("fulfilmentId")
    private Long fulfilmentId;

    @Field("productId")
    private Long productId;

    @Field("statusCheckAttempts")
    private Integer statusCheckAttempts;

    @Field("paymentStatus")
    private String paymentStatus;

    @Field("paymentState")
    private PaymentState paymentState;

    @Field("fulfilmentStatus")
    private Integer fulfilmentStatus;

    @Field("createdDate")
    private Date createdDate;

    @Field("updatedDate")
    private Date updatedDate;

    @Field("merchantTxnId")
    private String merchantTxnId;

    @Field("amount")
    private Float amount;
}