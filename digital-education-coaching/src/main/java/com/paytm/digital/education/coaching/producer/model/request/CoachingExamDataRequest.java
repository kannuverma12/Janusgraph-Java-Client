package com.paytm.digital.education.coaching.producer.model.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.enums.ExamType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;


@Data
@ApiModel
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CoachingExamDataRequest {

    @Min(value = 1)
    @ApiModelProperty(value = "unique existing id of the coaching exam, ignore for new record")
    private Long coachingExamId;

    @NotNull
    @Min(value = 1)
    @ApiModelProperty(value = "id of existing coaching institute")
    private Long instituteId;

    @NotNull
    @ApiModelProperty(value = "elements from predefined exam types")
    private ExamType examType;

    @NotEmpty
    @Size(max = 100)
    @ApiModelProperty(value = "name of the coaching exam")
    private String examName;

    @NotEmpty
    @Size(max = 1000)
    @ApiModelProperty(value = "exam description")
    private String examDescription;

    @NotNull
    @Min(value = 1)
    @ApiModelProperty(value = "id of existing coaching institute program")
    private Long programId;

    @NotNull
    @Min(value = 1)
    @ApiModelProperty(value = "id of existing stream")
    private Long streamId;

    @NotEmpty
    @Size(max = 50)
    @ApiModelProperty(value = "exam duration")
    private String examDuration;

    @NotNull
    @Min(value = 100)
    @ApiModelProperty(value = "maximum marks of exam")
    private Double maximumMarks;

    @Valid
    @ApiModelProperty(value = "exam dates")
    private List<LocalDateTime> examDate;

    @NotEmpty
    @Size(max = 500)
    @ApiModelProperty(value = "exam eligibility")
    private String eligibility;

    @Min(value = 0)
    @ApiModelProperty(value = "priority of coaching exam")
    private Integer priority = new Integer(0);

    @ApiModelProperty(value = "flag to enable/disable the coaching exam")
    private Boolean isEnabled = Boolean.TRUE;

}
