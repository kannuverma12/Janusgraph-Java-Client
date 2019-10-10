package com.paytm.digital.education.coaching.es.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.database.embedded.Currency;
import com.paytm.digital.education.enums.CourseLevel;
import com.paytm.digital.education.enums.CourseType;
import com.paytm.digital.education.enums.DurationType;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CoachingCourseSearch {

    private Long                           courseId;
    private String                         courseName;
    private Long                           coachingInstituteId;
    private String                         coachingInstituteName;
    private Integer                        globalPriority;
    private CourseType                     courseType;
    private Double                         originalPrice;
    private Double                         discountedPrice;
    private Currency                       currency;
    private CourseLevel                    level;
    private Map<String, Map<String, Long>> streams;
    private List<Long>                     streamIds;
    private List<String>                   streamNames;
    private Map<String, Map<String, Long>> exams;
    private List<Long>                     examIds;
    private List<String>                   examNames;
    private String                         eligibility;
    private Double                         courseDurationDays;
    private Integer                        duration;
    private DurationType                   durationType;
    private String                         merchantProductId;
    private Long                           paytmProductId;
    private Boolean                        isEnabled;
    private Boolean                        isDynamic;
    private String                         redirectUrl;
}
