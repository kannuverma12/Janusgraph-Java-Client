package com.paytm.digital.education.coaching.consumer.model.response;

import com.paytm.digital.education.coaching.consumer.model.dto.CoachingProgramFeature;
import com.paytm.digital.education.coaching.consumer.model.dto.CoachingProgramImportantDate;
import com.paytm.digital.education.coaching.consumer.model.dto.CoachingProgramSessionDetails;
import com.paytm.digital.education.coaching.consumer.model.dto.Exam;
import com.paytm.digital.education.coaching.consumer.model.dto.TopRanker;
import com.paytm.digital.education.enums.CourseType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GetCoachingProgramDetailsResponse {

    private long   coachingInstituteId;
    private String coachingInstituteName;

    private long       programId;
    private String     programName;
    private CourseType programType;
    private String     programLogo;
    private String     programDescription;
    private String     programPrice;

    private List<Exam> targetExams;
    private List<Exam> auxiliaryExams;
    private String     eligibility;
    private String     duration;

    private List<TopRanker> topRankers;

    private List<CoachingProgramImportantDate> importantDates;

    private List<CoachingProgramFeature> programFeatures;
    private List<String>                 programInclusions;

    private List<CoachingProgramSessionDetails> sessionDetails;

    private String syllabus;
    private String brochure;
}
