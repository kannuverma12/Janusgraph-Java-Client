package com.paytm.digital.education.coaching.producer.model.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class StreamDTO {

    private Long streamId;

    private String name;

    private Integer priority;

    private String logo;

    private Boolean isEnabled;

    private List<Long> topInstitutes;
}
