package com.paytm.digital.education.coaching.producer.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class PreviousExamPaperCreateRequest {

    @NotNull
    @ApiModelProperty(value = "id of the stream\n")
    public Long id;

    @NotNull
    @ApiModelProperty(value = "name of the stream\n")
    public Long examId;

    @NotNull
    @ApiModelProperty(
            value = "json data which governs the display pattern",
            example = "{\"bg_color\": \"aaaa\", \"text\" : \"management\"}\n")
    public LocalDate examDate;

    @NotNull
    @ApiModelProperty(value = "description of the stream\n", notes = "getting sheet data")
    public String fileLink;

    @NotNull public Integer priority;

    @NotNull public Boolean isEnabled = Boolean.TRUE;
}
