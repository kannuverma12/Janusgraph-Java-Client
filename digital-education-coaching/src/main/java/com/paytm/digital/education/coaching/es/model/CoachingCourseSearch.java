package com.paytm.digital.education.coaching.es.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.database.embedded.Currency;
import com.paytm.digital.education.enums.CourseLevel;
import com.paytm.digital.education.enums.CourseType;
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
    private String                         logo;
    private Integer                        globalPriority;
    private CourseType                     courseType;
    private Double                         price;
    private Currency                       currency;
    private CourseLevel                    courseLevel;
    private Map<String, Map<String, Long>> streams;
    private List<Long>                     streamIds;
    private List<String>                   streamNames;
    private Map<String, Map<String, Long>> exams;
    private List<Long>                     examIds;
    private List<String>                   examNames;
}
