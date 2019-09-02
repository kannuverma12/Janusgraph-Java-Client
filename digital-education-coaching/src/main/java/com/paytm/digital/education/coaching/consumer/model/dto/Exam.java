package com.paytm.digital.education.coaching.consumer.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Exam {

    private long   id;
    private String examFullName;
    private String examShortName;
    private String logo;
    private String admissionInto;
    private String conductedBy;
    private int    priority;
}
