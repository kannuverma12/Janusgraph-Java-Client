package com.paytm.digital.education.coaching.consumer.model.response.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.database.embedded.Currency;
import com.paytm.digital.education.enums.CourseLevel;
import com.paytm.digital.education.enums.CourseType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CoachingCourseData extends SearchBaseData {

    private Long        courseId;
    private String      courseName;
    private Long        coachingInstituteId;
    private String      coachingInstituteName;
    private String      logo;
    private CourseType  courseType;
    private Double      courseDurationDays;
    private Double      price;
    private Currency    currency;
    private CourseLevel courseLevel;
    private String      eligibility;
    private String      urlDisplayKey;

}
