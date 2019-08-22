package com.paytm.digital.education.coaching.producer.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.database.embedded.Faq;
import com.paytm.digital.education.database.embedded.KeyHighlight;
import com.paytm.digital.education.database.embedded.OfficialAddress;
import com.paytm.digital.education.enums.CourseType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
@ApiModel
public class CoachingInstituteDataRequest {

    @Size(min = 1)
    @ApiModelProperty(value = "unique existing institute id, should be ignored in case of new record")
    @JsonProperty("institute_id")
    private Long instituteId;

    @NotEmpty
    @Size(max = 100)
    @ApiModelProperty(value = "brand name of the coaching institute")
    @JsonProperty("brand_name")
    private String brandName;

    @NotEmpty
    @Size(max = 1000)
    @JsonProperty("about_institute")
    private String aboutInstitute;

    @NotNull
    @URL
    @ApiModelProperty(value = "url of institute logo image")
    private String logo;

    @NotEmpty
    @URL
    @ApiModelProperty(value = "institute cover images")
    private String coverImage;

    private List<KeyHighlight> highlights;

    @NotEmpty
    @JsonProperty("stream_ids")
    private List<Long> streamIds;

    @NotEmpty
    @JsonProperty("exam_ids")
    private List<Long> examIds;

    @NotNull
    @JsonProperty("priority")
    private Integer priority;

    @JsonProperty("is_enabled")
    @ApiModelProperty(value = "flag is enable/disable institute, default is true")
    private Boolean isEnabled = Boolean.TRUE;

    private List<Faq> faqs;

    @NotEmpty
    @JsonProperty("course_types")
    private List<CourseType> courseTypes;

    @Valid
    private OfficialAddress address;

    @NotEmpty
    @JsonProperty("establishment_year")
    @Pattern(regexp = "^(19|20)\\d{2}$")
    private String establishmentYear;

    @URL
    @NotEmpty
    @JsonProperty("brochure_url")
    private String brochureUrl;
}
