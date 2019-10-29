package com.paytm.digital.education.coaching.consumer.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ExamImportantDate {

    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMMM,yyyy")
    private Date dateStartRange;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMMM,yyyy")
    private Date dateEndRange;

    private Date         dateStartRangeTimestamp;
    private Date         dateEndRangeTimestamp;
    private String       type;
    private String       typeDisplayName;
    private List<String> modes;
    private String       monthDate;
    private Date         monthTimestamp;
    private String       certainity;
}
