package com.paytm.digital.education.coaching.producer.model.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.validator.PositiveElementsCollection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@ApiModel
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TopRankerDataRequest {

    @Min(value = 1)
    @ApiModelProperty(value = "unique existing rank id, should be ignored in case of new record")
    private Long topRankerId;

    @NotNull
    @Min(value = 1)
    @ApiModelProperty(value = "id of the coaching institute")
    private Long instituteId;

    @NotNull
    @Min(value = 1)
    @ApiModelProperty(value = "id of the coaching center")
    private Long centerId;

    @NotNull
    @Min(value = 1)
    @ApiModelProperty(value = "id of the target exam")
    private Long examId;

    @NotEmpty
    @Size(max = 50)
    @ApiModelProperty(value = "name of student")
    private String studentName;

    @URL
    @NotEmpty
    @ApiModelProperty(value = "student photo url")
    private String studentPhoto;

    @NotEmpty
    @PositiveElementsCollection
    @ApiModelProperty(value = "course ids studied")
    private List<Long> courseStudied;

    @NotEmpty
    @Size(max = 50)
    @ApiModelProperty(value = "batch info")
    private String batchInfo;

    @NotEmpty
    @Size(max = 50)
    @ApiModelProperty(value = "rank obtained")
    private String rankObtained;

    @NotEmpty
    @Pattern(regexp = "^(19|20)\\d{2}$")
    @ApiModelProperty(value = "exam year of the rank holder")
    private String examYear;

    @NotEmpty
    @Size(max = 50)
    @ApiModelProperty(value = "college taken")
    private String collegeAdmitted;


    @NotEmpty
    @Size(max = 100)
    @ApiModelProperty(value = "testimonial from rank holder")
    private String testimonial;


    @NotNull
    @Min(value = 1)
    @ApiModelProperty(value = "priority across all the existing top ranker")
    private Integer priority;

    @NotNull
    @ApiModelProperty(value = "flag to enable/disable the top ranker")
    private Boolean isEnabled = Boolean.TRUE;
}
