package com.paytm.digital.education.coaching.consumer.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.enums.CourseType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TopCoachingCoursesForExam {

    private Long       courseId;
    private Long       coachingInstituteId;
    private String     coachingInstituteName;
    private String     logo;
    private String     courseName;
    private String     eligibility;
    private String     durationMonths;
    private CourseType courseType;
    private String     urlDisplayKey;
}
