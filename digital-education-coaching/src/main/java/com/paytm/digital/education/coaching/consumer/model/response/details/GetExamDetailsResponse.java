package com.paytm.digital.education.coaching.consumer.model.response.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.coaching.consumer.model.dto.ExamAdditionalInfo;
import com.paytm.digital.education.coaching.consumer.model.dto.ExamImportantDate;
import com.paytm.digital.education.coaching.consumer.model.dto.ImportantDatesBannerDetails;
import com.paytm.digital.education.coaching.consumer.model.dto.TopCoachingCourses;
import com.paytm.digital.education.coaching.consumer.model.dto.TopCoachingInstitutes;
import com.paytm.digital.education.dto.detail.Event;
import com.paytm.digital.education.dto.detail.Syllabus;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
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
    private TopCoachingCourses          topCoachingCourses;
    private ExamAdditionalInfo          additionalInfo;
    private List<String>                sections;
    private ImportantDatesBannerDetails importantDatesBannerDetails;
    private List<SectionDataHolder>     sectionsList;
    private List<Syllabus>              syllabus;
    private List<Event>                 importantDates;
    private String                      eligibility;
    private String                      examPattern;
    private String                      result;
    private String                      cutoff;
    private List<String>                documentsCounselling;
    private String                      admitCard;
    private String                      applicationForm;

}
