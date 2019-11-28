package com.paytm.digital.education.explore.response.dto.notification;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class NotificationResponseDTO {

    @JsonProperty("article_title")
    private String articleTitle;

    @JsonProperty("article_id")
    private Integer articleId;

    @JsonProperty("article_url")
    private String articleUrl;

    @JsonProperty("article_thumb_url")
    private String articleThumbUrl;

    @JsonProperty("article_description")
    private String articleDescription;

    @JsonProperty("exam_id")
    private Integer examId;

    @JsonProperty("stream")
    private String stream;

    @JsonProperty("push_title")
    private String pushTitle;

    @JsonProperty("push_content")
    private String pushContent;

    @JsonProperty("merchant")
    private String merchant;

    @JsonProperty("paytm_stream_id")
    private Long paytmStreamId;

    @JsonProperty("merchant_updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime merchantUpdatedAt;

    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updatedAt;
}


