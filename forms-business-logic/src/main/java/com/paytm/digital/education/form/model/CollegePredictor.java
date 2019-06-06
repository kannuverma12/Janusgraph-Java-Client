package com.paytm.digital.education.form.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Document("predictor_list")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CollegePredictor {

    @Id
    @JsonIgnore
    @Field("_id")
    private ObjectId mongoId;

    @Field("id")
    @JsonProperty("id")
    private Long merchantSku;

    @Field("status")
    @JsonProperty("status")
    private String status;

    @Field("image")
    @JsonProperty("image")
    private String image;

    @Field("description")
    @JsonProperty("description")
    private String description;

    @Field("title")
    @JsonProperty("title")
    private String title;

    @Field("type")
    @JsonProperty("type")
    private String type;

    @Field("currency")
    @JsonProperty("currency")
    private String currency;

    @Field("price")
    @JsonProperty("price")
    private Integer price;

    @Field("offered_price")
    @JsonProperty("offered_price")
    private Integer offeredPrice;

    @Field("long_description")
    @JsonProperty("long_description")
    private List<String> longDescription;

    @Field("paytm_price")
    @JsonProperty("paytm_price")
    private Integer paytmPrice;

    @Field("pid")
    @JsonProperty("pid")
    private Long pid;

}
