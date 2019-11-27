package com.paytm.digital.education.explore.request.dto.notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@Validated
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MerchantNotificationRequest {

    @NotBlank
    @JsonProperty("merchant")
    @ApiModelProperty(value = "Merchant name")
    private String merchant;

    @NotBlank
    @JsonProperty("article_title")
    @ApiModelProperty(value = "Article title")
    private String articleTitle;

    @Min(1)
    @JsonProperty("article_id")
    @ApiModelProperty(value = "Article id")
    private Integer articleId;

    @NotBlank
    @JsonProperty("article_url")
    @ApiModelProperty(value = "Article url")
    private String articleUrl;

    @NotBlank
    @JsonProperty("article_thumb_url")
    @ApiModelProperty(value = "Article thumbnail url")
    private String articleThumbUrl;

    @NotBlank
    @JsonProperty("article_description")
    @ApiModelProperty(value = "Article description")
    private String articleDescription;

    @Min(1)
    @JsonProperty("exam_id")
    @ApiModelProperty(value = "exam id ")
    private Integer examId;

    @NotBlank
    @JsonProperty("stream")
    @ApiModelProperty(value = "Article stream")
    private String stream;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @NotNull @Past
    @JsonProperty("updated_at")
    @ApiModelProperty(value = "Article updated at")
    private LocalDateTime updatedAt;

    @NotBlank
    @JsonProperty("push_title")
    @ApiModelProperty(value = "Article push title")
    private String pushTitle;

    @NotBlank
    @JsonProperty("push_content")
    @ApiModelProperty(value = "Article push content")
    private String pushContent;

}
