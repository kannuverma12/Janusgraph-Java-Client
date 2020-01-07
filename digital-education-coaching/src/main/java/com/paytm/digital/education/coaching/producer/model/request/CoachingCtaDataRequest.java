package com.paytm.digital.education.coaching.producer.model.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.coaching.enums.CtaType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Validated
public class CoachingCtaDataRequest {

    @Min(value = 1)
    @ApiModelProperty(value = "unique existing id of the coaching cta, ignore for new record")
    private Long ctaId;

    @NotEmpty
    @Size(max = 100)
    @ApiModelProperty(value = "name of coaching cta")
    private String name;

    @NotBlank
    @Size(max = 250)
    @ApiModelProperty(value = "description of coaching cta")
    private String description;

    @NotNull
    @ApiModelProperty(value = "elements from predefined cta types")
    private CtaType ctaType;

    @URL
    @ApiModelProperty(value = "cta logo url")
    private String logoUrl;

    @URL
    @ApiModelProperty(value = "cta action url")
    private String url;

    @ApiModelProperty(value = "map of key value pair associated with cta")
    private List<String> properties;
}
