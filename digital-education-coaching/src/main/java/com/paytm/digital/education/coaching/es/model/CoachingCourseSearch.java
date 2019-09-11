package com.paytm.digital.education.coaching.es.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.paytm.digital.education.database.embedded.Currency;
import com.paytm.digital.education.enums.CourseLevel;
import com.paytm.digital.education.enums.CourseType;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CoachingCourseSearch {

    private Long                              courseId;
    private String                            courseName;
    private Long                              coachingInstituteId;
    private String                            coachingInstituteName;
    private String                            logo;
    private Integer                           globalPriority;
    private CourseType                        courseType;
    private Double                            courseDurationDays;
    private Double                            price;
    private Currency                          currency;
    private CourseLevel                       courseLevel;
    private String                            eligibility;
    private Map<String, Map<String, Long>>    streams;
    private List<Long>                        streamIds;
    private List<String>                      streamNames;
    private Map<String, Map<String, Long>>    exams;
    private List<Long>                        examIds;
    private List<String>                      examNames;
    private String                            info;
    private String                            description;
    private String                            language;
    private String                            syllabus;
    private String                            brochure;
    private List<CoachingCourseImportantDate> importantDates;
    private Boolean                           isCertificateAvailable;
    private Boolean                           isDoubtSolvingForumAvailable;
    private Boolean                           isProgressAnalysisAvailable;
    private Boolean                           isRankAnalysisAvailable;
    private Integer                           testCount;
    private Integer                           testDuration;
    private Integer                           testQuestionCount;
    private Integer                           testPracticePaperCount;
    private Integer                           distanceLearningBooksCount;
    private Integer                           distanceLearningSolvedPaperCount;
    private Integer                           distanceLearningAssignmentCount;
    private Integer                           elearningLectureCount;
    private Integer                           elearningLectureDuration;
    private Integer                           elearningOnlineTestCount;
    private Integer                           elearningPracticePaperCount;
    private Integer                           classroomLectureCount;
    private Integer                           classroomLectureDuration;
    private Integer                           classroomTestCount;
    private Double                            classroomTeacherStudentRatio;
}
