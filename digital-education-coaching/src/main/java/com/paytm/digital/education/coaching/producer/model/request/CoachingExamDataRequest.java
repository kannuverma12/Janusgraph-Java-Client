package com.paytm.digital.education.coaching.producer.model.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.enums.ExamType;
import com.paytm.digital.education.validator.PositiveElementsCollection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Validated
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
    @Size(max = 20)
    @ApiModelProperty(value = "name of the coaching exam")
    private String examName;

    @NotEmpty
    @Size(max = 250)
    @ApiModelProperty(value = "exam description")
    private String examDescription;

    @NotEmpty
    @PositiveElementsCollection
    @ApiModelProperty(value = "id of existing coaching institute program")
    private List<Long> courseIds;

    @NotEmpty
    @PositiveElementsCollection
    @ApiModelProperty(value = "id of existing stream")
    private List<Long> streamIds;

    @Size(max = 20)
    @ApiModelProperty(value = "exam duration")
    private String examDuration;

    @Min(value = 10)
    @Max(value = 9999)
    @ApiModelProperty(value = "maximum marks of exam")
    private Double maximumMarks;

    @ApiModelProperty(value = "exam date")
    // TODO : add validation as dd/mm/yy
    private LocalDateTime examDate;

    @NotEmpty
    @Size(max = 40)
    @ApiModelProperty(value = "exam eligibility")
    private String eligibility;

    @NotNull
    @Min(value = 1)
    @ApiModelProperty(value = "priority of coaching exam")
    private Integer priority;

    @ApiModelProperty(value = "flag to enable/disable the coaching exam")
    private Boolean isEnabled = Boolean.TRUE;

    @Min(value = 1)
    @ApiModelProperty(value = "questions count for the given coaching exam")
    private Long questionCount;

}
