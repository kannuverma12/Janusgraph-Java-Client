package com.paytm.digital.education.coaching.ingestion.model.googleform;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CoachingCourseForm {

    private Long    courseId;
    private String  courseName;
    private Long    instituteId;
    private String  courseType;
    private String  streamIds;
    private String  examPreparedIds;
    private Integer courseDurationValue;
    private String  courseDurationType;
    private String  eligibilityCriteria;
    private String  courseIntroduction;
    private String  courseDescription;
    private String  featureIds;
    private Double  price;
    private String  levelOfEducation;
    private String  language;
    private String  syllabus;
    private String courseCovers;

    private String howToUse1;
    private String howToUse2;
    private String howToUse3;
    private String howToUse4;

    private String importantDateKey1;
    private String importantDateVal1;
    private String importantDateKey2;
    private String importantDateVal2;
    private String importantDateKey3;
    private String importantDateVal3;

    private String  certificate;
    private String  doubtSolvingDiscussionAvailable;
    private String  testAnalysisAndComparisonReportAvailable;
    private String  allIndiaRankAvailable;
    private String  typeOfCourse;
    private Integer numberOfTestsInTestSeries;
    private Integer durationOfEachTestInTestSeries;
    private Integer numberOfQuestionsInTestSeries;
    private Integer numberOfDailyPracticeTestsAvailableInTestSeries;
    private Integer numberOfBooks;
    private Integer numberOfSolvedPapers;
    private Integer numberOfAssignments;
    private Integer numberOfLiveLectures;
    private Integer durationOfEachLiveLecture;
    private Integer numberOfTestsAvailableOnline;
    private Integer numberOfDailyPracticePaperAvailableOnline;
    private Integer numberOfClassroomLectures;
    private Integer durationOfClassroomLectures;
    private Integer numberOfClassroomTests;
    private String  teacherToStudentRatio;

    private Integer globalPriority;
    private String  statusActive;
}
