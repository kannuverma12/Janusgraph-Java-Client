package com.paytm.digital.education.coaching.consumer.model.response.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.coaching.consumer.model.dto.Faq;
import com.paytm.digital.education.coaching.consumer.model.dto.MockTest;
import com.paytm.digital.education.coaching.consumer.model.dto.TopCoachingCourses;
import com.paytm.digital.education.coaching.consumer.model.dto.TopExamsInstitute;
import com.paytm.digital.education.coaching.consumer.model.dto.TopRankers;
import com.paytm.digital.education.coaching.consumer.model.dto.TopStreams;
import com.paytm.digital.education.coaching.consumer.model.dto.coachinginstitute.CenterAndBrochureInfo;
import com.paytm.digital.education.coaching.consumer.model.dto.coachinginstitute.CoachingCourseTypeInfo;
import com.paytm.digital.education.coaching.consumer.model.dto.coachinginstitute.InstituteHighlight;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GetCoachingInstituteDetailsResponse {

    private long                     instituteId;
    private String                   instituteName;
    private String                   imageUrl;
    private String                   logo;
    private String                   description;
    private List<InstituteHighlight> instituteHighlights;
    private CenterAndBrochureInfo    centerAndBrochureInfo;
    private TopStreams               streams;
    private TopExamsInstitute        exams;
    private TopRankers               topRankers;
    private List<Faq>                faqs;
    private CoachingCourseTypeInfo   coachingCourseTypes;
    private TopCoachingCourses       topCoachingCourses;
    private MockTest                 mockTest;
    private List<String>             sections;
}
