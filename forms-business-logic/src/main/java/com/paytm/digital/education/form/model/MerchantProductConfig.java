package com.paytm.digital.education.form.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Map;


@Data
@Document("merchantProductConfig")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MerchantProductConfig {

    @Field("merchantId")
    private String merchantId;

    @Id
    @Field("productId")
    private String productId;

    @Field("createdDate")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date createdDate;

    @Field("updatedDate")
    private Date updatedDate;

    @Field("data")
    private Map<String, Object> data;
}