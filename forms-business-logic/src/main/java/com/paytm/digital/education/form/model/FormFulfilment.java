package com.paytm.digital.education.form.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FormFulfilment {
    @Field("orderId")
    private String orderId;

    @Field("itemId")
    private String itemId;

    @Field("fulfilmentId")
    private String fulfilmentId;

    @Field("productId")
    private String productId;

    @Field("statusCheckAttempts")
    private int statusCheckAttempts;

    @Field("paymentStatus")
    private String paymentStatus;

    @Field("createdDate")
    private Date createdDate;

    @Field("updatedDate")
    private Date updatedDate;

    @Field("merchantTxnId")
    private String merchantTxnId;
}