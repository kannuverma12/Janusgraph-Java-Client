package com.paytm.digital.education.coaching.producer.model.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.database.embedded.KeyHighlight;
import com.paytm.digital.education.database.embedded.OfficialAddress;
import com.paytm.digital.education.enums.CourseType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CoachingInstituteDTO {

    private Long instituteId;

    private String brandName;

    private String aboutInstitute;

    private String logo;

    private String coverImage;

    private List<KeyHighlight> highlights;

    private List<Long> streamIds;

    private List<Long> examIds;

    private Integer priority;

    private Boolean isEnabled;

    private List<CourseType> courseTypes;

    private OfficialAddress address;

    private String establishmentYear;

    private String brochureUrl;
}
