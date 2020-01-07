package com.paytm.digital.education.explore.response.dto.articles;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.database.entity.MerchantArticle;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class NewsArticleResponse implements Serializable {

    private static final long serialVersionUID = 7565703583217345951L;

    private String title;
    private List<NewsArticleData> data;
}
