package com.paytm.digital.education.coaching.consumer.model.response.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.coaching.consumer.model.dto.Exam;
import com.paytm.digital.education.coaching.consumer.model.dto.TopRankers;
import com.paytm.digital.education.coaching.consumer.model.dto.coachingcourse.CoachingCourseDetails;
import com.paytm.digital.education.coaching.consumer.model.dto.coachingcourse.CoachingCourseFeatures;
import com.paytm.digital.education.coaching.consumer.model.dto.coachingcourse.CoachingCourseFee;
import com.paytm.digital.education.coaching.consumer.model.dto.coachingcourse.CoachingCourseImportantDates;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GetCoachingCourseDetailsResponse {

    private long   courseId;
    private String courseName;
    private String courseLogo;
    private String courseDescription;

    private long   coachingInstituteId;
    private String coachingInstituteName;

    private Exam   targetExam;
    private String eligibility;
    private String duration;

    private TopRankers                   topRankers;
    private CoachingCourseDetails        coachingCourseDetails;
    private CoachingCourseImportantDates importantDates;
    private CoachingCourseFeatures       courseFeatures;
    private CoachingCourseFee            coachingCourseFee;

    private List<String> sections;
}
