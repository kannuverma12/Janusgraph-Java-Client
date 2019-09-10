package com.paytm.digital.education.coaching.consumer.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.coaching.consumer.model.dto.ImportantDatesBannerDetails;
import com.paytm.digital.education.coaching.consumer.model.dto.TopCoachingCourses;
import com.paytm.digital.education.coaching.consumer.model.dto.TopCoachingInstitutes;
import com.paytm.digital.education.coaching.consumer.model.dto.TopExams;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GetStreamDetailsResponse {

    private long                        streamId;
    private String                      streamName;
    private TopExams                    topExams;
    private TopCoachingInstitutes       topCoachingInstitutes;
    private TopCoachingCourses          topCoachingCourses;
    private List<String>                sections;
    private ImportantDatesBannerDetails importantDatesBannerDetails;
}
