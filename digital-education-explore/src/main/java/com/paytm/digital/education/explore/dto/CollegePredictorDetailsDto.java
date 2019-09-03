package com.paytm.digital.education.explore.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public class CollegePredictorDetailsDto {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("status")
    private String status;

    @JsonProperty("image")
    private String image;

    @JsonProperty("description")
    private String description;

    @JsonProperty("title")
    private String title;

    @JsonProperty("type")
    private String type;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("price")
    private Integer price;

    @JsonProperty("offered_price")
    private Integer offeredPrice;

    @JsonProperty("long_description")
    private List<String> longDescription;

    @JsonProperty("paytm_price")
    private Integer paytmPrice;

    @JsonProperty("pid")
    private Long pid;
}
