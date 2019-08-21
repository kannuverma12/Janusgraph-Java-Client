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

    @JsonProperty("offeredPrice")
    private Integer offeredPrice;

    @JsonProperty("longDescription")
    private List<String> longDescription;

    @JsonProperty("paytmPrice")
    private Integer paytmPrice;

    @JsonProperty("pid")
    private Long pid;
}
