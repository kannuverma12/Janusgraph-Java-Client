package com.paytm.digital.education.explore.response.dto.articles;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MerchantArticleResponse {

    @JsonProperty("status")
    private Integer status ;

    @JsonProperty("message")
    private String message;

    @JsonProperty("merchant_article")
    private ArticleResponseDTO articleResponseDTO;

}
