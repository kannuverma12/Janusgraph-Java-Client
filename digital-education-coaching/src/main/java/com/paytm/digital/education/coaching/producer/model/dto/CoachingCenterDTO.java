package com.paytm.digital.education.coaching.producer.model.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.database.embedded.OfficialAddress;
import com.paytm.digital.education.enums.CourseType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CoachingCenterDTO {

    private Long centerId;

    private Long instituteId;

    private String officialName;

    private OfficialAddress officialAddress;

    private List<CourseType> courseTypes;
}
