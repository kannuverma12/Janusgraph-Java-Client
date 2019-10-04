package com.paytm.digital.education.coaching.consumer.model.response.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.coaching.consumer.model.dto.ExamAdditionalInfo;
import com.paytm.digital.education.coaching.consumer.model.dto.ExamImportantDate;
import com.paytm.digital.education.coaching.consumer.model.dto.ImportantDatesBannerDetails;
import com.paytm.digital.education.coaching.consumer.model.dto.TopCoachingCourses;
import com.paytm.digital.education.coaching.consumer.model.dto.TopCoachingInstitutes;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GetExamDetailsResponse {

    private long                        examId;
    private String                      examFullName;
    private String                      examShortName;
    private String                      urlDisplayKey;
    private String                      examDescription;
    private TopCoachingInstitutes       topCoachingInstitutes;
    private List<ExamImportantDate>     importantDates;
    private TopCoachingCourses          topCoachingCourses;
    private ExamAdditionalInfo          additionalInfo;
    private List<String>                sections;
    private ImportantDatesBannerDetails importantDatesBannerDetails;
    private List<String>                filters;
}
