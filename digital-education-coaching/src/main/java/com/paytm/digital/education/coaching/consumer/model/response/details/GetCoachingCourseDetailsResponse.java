package com.paytm.digital.education.coaching.consumer.model.response.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.coaching.consumer.model.dto.MockTest;
import com.paytm.digital.education.coaching.consumer.model.dto.TopRankers;
import com.paytm.digital.education.coaching.consumer.model.dto.coachingcourse.CoachingCourseDetails;
import com.paytm.digital.education.coaching.consumer.model.dto.coachingcourse.CoachingCourseFeatures;
import com.paytm.digital.education.coaching.consumer.model.dto.coachingcourse.CoachingCourseFee;
import com.paytm.digital.education.coaching.consumer.model.dto.coachingcourse.CoachingCourseHighlight;
import com.paytm.digital.education.coaching.consumer.model.dto.coachingcourse.CoachingCourseImportantDates;
import com.paytm.digital.education.coaching.consumer.model.dto.coachingcourse.CourseGetStarted;
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

    private long   paytmProductId;
    private String merchantProductId;
    private String categoryId;
    private String educationVertical;

    private long   coachingInstituteId;
    private String coachingInstituteName;

    private List<CoachingCourseHighlight> courseHighlights;

    private TopRankers                   topRankers;
    private CoachingCourseDetails        coachingCourseDetails;
    private CoachingCourseImportantDates importantDates;
    private CoachingCourseFeatures       courseFeatures;
    private MockTest                     mockTest;
    private CoachingCourseFee            coachingCourseFee;
    private CourseGetStarted             courseGetStarted;

    private List<String> sections;
}
