package com.paytm.digital.education.coaching.consumer.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.coaching.consumer.model.dto.Exam;
import com.paytm.digital.education.coaching.consumer.model.dto.Faq;
import com.paytm.digital.education.coaching.consumer.model.dto.InstituteHighLight;
import com.paytm.digital.education.coaching.consumer.model.dto.Stream;
import com.paytm.digital.education.coaching.consumer.model.dto.TopRanker;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GetCoachingInstituteDetailsResponse {

    private long                             instituteId;
    private String                           instituteName;
    private String                           imageUrl;
    private String                           description;
    private List<InstituteHighLight>         instituteHighlights;
    private List<Stream>                     streams;
    private List<Exam>                       exams;
    private List<TopRanker>                  topRankers;
    private List<Faq>                        faqs;
    private List<CoachingCourseTypeResponse> coachingCourseTypes;
    private List<String>                     sections;
}
