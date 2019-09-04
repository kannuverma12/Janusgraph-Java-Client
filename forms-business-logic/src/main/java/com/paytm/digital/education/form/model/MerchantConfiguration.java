package com.paytm.digital.education.form.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Map;


@Data
@Document("merchantConfiguration")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MerchantConfiguration {

    @Id
    @Field("merchantId")
    @JsonProperty("merchant_id")
    private String merchantId;

    @Field("screenConfig")
    @JsonProperty("screen_config")
    private Map<String, Object> postOrderScreenConfig;

    @Field("createdDate")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date createdDate;

    @Field("updatedDate")
    private Date updatedDate;

    @Field("data")
    @JsonProperty("data")
    private Map<String, Object> data;
}