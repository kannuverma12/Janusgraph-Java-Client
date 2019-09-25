package com.paytm.digital.education.admin.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RankingResponse {

    @JsonProperty("entity")
    private String entity;

    @JsonProperty("rankings")
    List<EntityRankingResponse> rankings;

    @JsonProperty("message")
    private String message;

    @JsonProperty("status")
    private Integer status;
}
