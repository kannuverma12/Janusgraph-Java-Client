package com.paytm.digital.education.coaching.consumer.model.response;

import com.paytm.digital.education.coaching.consumer.model.dto.CoachingCourse;
import com.paytm.digital.education.coaching.consumer.model.dto.CoachingInstitute;
import com.paytm.digital.education.coaching.consumer.model.dto.Exam;
import com.paytm.digital.education.coaching.consumer.model.dto.ExamImportantDate;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetStreamDetailsResponse {

    private long                    streamId;
    private String                  streamName;
    private List<Exam>              topExams;
    private List<ExamImportantDate> examImportantDates;
    private List<CoachingInstitute> topCoachingInstitutes;
    private List<CoachingCourse>    topCoachingCourses;
}
