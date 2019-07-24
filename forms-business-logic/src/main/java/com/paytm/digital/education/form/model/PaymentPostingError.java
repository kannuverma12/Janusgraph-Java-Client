package com.paytm.digital.education.form.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.paytm.digital.education.form.request.PaymentPostingItemRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentPostingError {

    @Field("refId")
    String refId;

    @Field("requestId")
    String requestId;

    @Field("errorMessage")
    String errorMessage;

    @Field("request")
    PaymentPostingItemRequest request;

    @Field("updatedAt")
    Date updatedAt;
}
