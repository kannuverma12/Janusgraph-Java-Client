package com.paytm.digital.education.coaching.producer.model.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.coaching.producer.model.embedded.Faq;
import com.paytm.digital.education.coaching.producer.model.embedded.KeyHighlight;
import com.paytm.digital.education.coaching.producer.model.embedded.OfficialAddress;
import com.paytm.digital.education.enums.CourseLevel;
import com.paytm.digital.education.enums.CourseType;
import com.paytm.digital.education.validator.PositiveElementsCollection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@ApiModel
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
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

    @URL
    @NotNull
    @ApiModelProperty(value = "url of institute logo image")
    private String logo;

    @URL
    @NotEmpty
    @ApiModelProperty(value = "institute cover image")
    private String coverImage;

    @Valid
    private List<KeyHighlight> highlights;

    @NotEmpty
    @PositiveElementsCollection
    private List<Long> streamIds;

    @NotEmpty
    @PositiveElementsCollection
    private List<Long> examIds;

    @NotNull
    @Min(value = 1)
    @ApiModelProperty(value = "priority of coaching institute")
    private Integer priority;

    @ApiModelProperty(value = "flag is enable/disable institute, default is true")
    private Boolean isEnabled = Boolean.TRUE;

    @NotEmpty
    @ApiModelProperty(value = "elements from predefined course types")
    private List<CourseType> courseTypes;

    @NotEmpty
    @ApiModelProperty(value = "elements from predefined course levels")
    private List<CourseLevel> courseLevels;

    @Valid
    private OfficialAddress officialAddress;

    @Pattern(regexp = "^(19|20)\\d{2}$")
    private String establishmentYear;

    @URL
    @NotEmpty
    private String brochureUrl;

    @Valid
    private List<Faq> faqs;

    @URL
    private String extraInfo1;

    @Size(max = 200)
    private String extraInfo2;

}
