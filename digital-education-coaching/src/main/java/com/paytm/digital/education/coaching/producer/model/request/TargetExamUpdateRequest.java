package com.paytm.digital.education.coaching.producer.model.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.validator.PositiveElementsCollection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Validated
public class TargetExamUpdateRequest {

    @NotNull
    @Min(value = 1)
    @ApiModelProperty(value = "unique existing id of the target exam")
    private Long examId;

    @NotEmpty
    @UniqueElements
    @PositiveElementsCollection
    private List<Long> streamIds;

    @NotNull
    @Min(value = 1)
    @ApiModelProperty(value = "priority across all the existing target exams")
    private Integer priority;

}
