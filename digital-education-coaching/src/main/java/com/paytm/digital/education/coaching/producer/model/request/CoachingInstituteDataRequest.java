package com.paytm.digital.education.coaching.producer.model.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.coaching.producer.model.embedded.Faq;
import com.paytm.digital.education.coaching.producer.model.embedded.KeyHighlight;
import com.paytm.digital.education.coaching.producer.model.embedded.OfficialAddress;
import com.paytm.digital.education.enums.CourseLevel;
import com.paytm.digital.education.enums.CourseType;
import com.paytm.digital.education.validator.PastYear;
import com.paytm.digital.education.validator.PositiveElementsCollection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Min;
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
public class CoachingInstituteDataRequest {

    @Min(value = 1)
    @ApiModelProperty(value = "unique existing institute id, should be ignored in case of new record")
    private Long instituteId;

    @NotEmpty
    @Size(max = 100)
    @ApiModelProperty(value = "brand name of the coaching institute")
    private String brandName;

    @NotEmpty
    @Size(max = 250)
    @ApiModelProperty(value = "description about coaching institute")
    private String aboutInstitute;

    @NotEmpty
    @ApiModelProperty(value = "url of institute logo image")
    private String logo;

    @NotEmpty
    @ApiModelProperty(value = "institute cover image")
    private String coverImage;

    @Valid
    @UniqueElements
    private List<KeyHighlight> highlights;

    @NotEmpty
    @UniqueElements
    @PositiveElementsCollection
    private List<Long> streamIds;

    @NotEmpty
    @UniqueElements
    @PositiveElementsCollection
    private List<Long> examIds;

    @NotNull
    @Min(value = 1)
    @ApiModelProperty(value = "priority of coaching institute")
    private Integer priority;

    @ApiModelProperty(value = "flag is enable/disable institute, default is true")
    private Boolean isEnabled = Boolean.TRUE;

    @NotEmpty
    @UniqueElements
    @ApiModelProperty(value = "elements from predefined course types")
    private List<CourseType> courseTypes;

    @NotEmpty
    @UniqueElements
    @ApiModelProperty(value = "elements from predefined course levels")
    private List<CourseLevel> courseLevels;

    @Valid
    private OfficialAddress officialAddress;

    @PastYear
    private String establishmentYear;

    @URL
    @NotEmpty
    private String brochureUrl;

    @Valid
    @UniqueElements
    private List<Faq> faqs;

    @NotEmpty
    private String paytmMerchantId;

    private String moreInfo1;
    private String moreInfo2;
    private String moreInfo3;
    private String moreInfo4;

}
