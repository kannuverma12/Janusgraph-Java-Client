package com.paytm.digital.education.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document("merchant_notification")
public class MerchantNotification extends Base {

    @Id
    @Field("_id")
    @JsonIgnore
    private ObjectId id;

    @Field("article_title")
    private String articleTitle;

    @Field("article_id")
    private Integer articleId;

    @Field("article_url")
    private String articleUrl;

    @Field("article_thumb_url")
    private String articleThumbUrl;

    @Field("article_description")
    private String articleDescription;

    @Field("exam_id")
    private Integer examId;

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
    @LastModifiedDate
    @JsonIgnore
    private LocalDateTime merchantUpdatedAt;

}

