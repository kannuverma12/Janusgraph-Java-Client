package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Document("merchant_articles")
public class MerchantArticle extends Base implements Serializable {

    private static final long serialVersionUID = 51591934474256669L;

    @Id
    @Field("_id")
    @JsonIgnore
    private ObjectId id;

    @Field("article_title")
    private String articleTitle;

    @Field("article_id")
    private Long articleId;

    @Field("article_url")
    @Indexed(unique = true)
    private String articleUrl;

    @Field("article_thumb_url")
    private String articleThumbUrl;

    @Field("article_description")
    private String articleDescription;

    @Field("exam_id")
    private Long examId;

    @Field("stream")
    private String stream;

    @Field("push_title")
    private String pushTitle;

    @Field("push_content")
    private String pushContent;

    @Field("merchant")
    private String merchant;

    @Field("paytm_stream_id")
    private Long paytmStreamId;

    @Field("merchant_updated_at")
    @JsonIgnore
    private LocalDateTime merchantUpdatedAt;

}

