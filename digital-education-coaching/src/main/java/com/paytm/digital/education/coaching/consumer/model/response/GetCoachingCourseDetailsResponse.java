package com.paytm.digital.education.coaching.consumer.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.coaching.consumer.model.dto.CoachingCourseImportantDate;
import com.paytm.digital.education.coaching.consumer.model.dto.Exam;
import com.paytm.digital.education.coaching.consumer.model.dto.TopRanker;
import com.paytm.digital.education.database.embedded.CoachingCourseFeature;
import com.paytm.digital.education.database.embedded.CoachingCourseSessionDetails;
import com.paytm.digital.education.database.embedded.Currency;
import com.paytm.digital.education.enums.CourseType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GetCoachingCourseDetailsResponse {

    private long       courseId;
    private String     courseName;
    private CourseType courseType;
    private String     courseLogo;
    private String     courseDescription;
    private Double     coursePrice;
    private Currency   currency;

    private long   coachingInstituteId;
    private String coachingInstituteName;

    private List<Exam> targetExams;
    private List<Exam> auxiliaryExams;
    private String     eligibility;
    private String     duration;

    private List<TopRanker> topRankers;

    private List<CoachingCourseImportantDate> importantDates;

    private List<CoachingCourseFeature> courseFeatures;
    private List<String>                courseInclusions;

    private List<CoachingCourseSessionDetails> sessionDetails;

    private String syllabus;
    private String brochure;
}
