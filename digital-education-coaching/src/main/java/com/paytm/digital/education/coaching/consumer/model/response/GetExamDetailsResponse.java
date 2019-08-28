package com.paytm.digital.education.coaching.consumer.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paytm.digital.education.coaching.consumer.model.dto.CoachingInstitute;
import com.paytm.digital.education.coaching.consumer.model.dto.ExamAdditionalInfo;
import com.paytm.digital.education.coaching.consumer.model.dto.ExamImportantDate;
import com.paytm.digital.education.coaching.consumer.model.dto.TopCoachingCoursesForExam;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetExamDetailsResponse {

    @JsonProperty("exam_id")
    private long examId;

    @JsonProperty("exam_full_name")
    private String examFullName;

    @JsonProperty("exam_short_name")
    private String examShortName;

    @JsonProperty("url_display_key")
    private String urlDisplayKey;

    @JsonProperty("exam_description")
    private String examDescription;

    @JsonProperty("top_coaching_institutes")
    private List<CoachingInstitute> topCoachingInstitutes;

    @JsonProperty("important_dates")
    private List<ExamImportantDate> importantDates;

    @JsonProperty("top_coaching_courses")
    private List<TopCoachingCoursesForExam> topCoachingCourses;

    @JsonProperty("additional_info")
    private List<ExamAdditionalInfo> additionalInfo;
}
