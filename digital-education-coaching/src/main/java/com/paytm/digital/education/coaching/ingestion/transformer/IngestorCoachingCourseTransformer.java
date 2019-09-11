package com.paytm.digital.education.coaching.ingestion.transformer;

import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingCourseForm;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCourseDataRequest;
import com.paytm.digital.education.enums.CourseLevel;
import com.paytm.digital.education.enums.CourseType;
import com.paytm.digital.education.enums.DurationType;
import com.paytm.digital.education.enums.Language;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class IngestorCoachingCourseTransformer {

    public static CoachingCourseDataRequest convert(final CoachingCourseForm form) {
        if (null == form) {
            return null;
        }
        return CoachingCourseDataRequest.builder()
                .courseId(form.getCourseId())
                .name(form.getCourseName())
                .instituteId(form.getInstituteId())
                .courseType(CourseType.fromString(form.getCourseType()))
                .streamIds(IngestorCommonTransformer.convertStringToListOfLong(
                        form.getStreamIds()))
                .primaryExamIds(IngestorCommonTransformer.convertStringToListOfLong(
                        form.getExamPreparedIds()))
                .duration(form.getCourseDurationValue())
                .durationType(DurationType.fromString(form.getCourseDurationType()))
                .eligibility(form.getEligibilityCriteria())
                .info(form.getCourseIntroduction())
                .description(form.getCourseDescription())
                .courseFeatureIds(IngestorCommonTransformer.convertStringToListOfLong(
                        form.getFeatureIds()))
                .price(form.getPrice())
                .courseLevel(CourseLevel.fromString(form.getLevelOfEducation()))
                .language(Language.fromString(form.getLanguage()))
                .syllabusAndBrochure(form.getSyllabus())
                .priority(form.getGlobalPriority())
                .isCertificateAvailable(IngestorCommonTransformer.convertStringToBoolean(
                        form.getCertificate()))
                .isDoubtSolvingForumAvailable(IngestorCommonTransformer.convertStringToBoolean(
                        form.getDoubtSolvingDiscussionAvailable()))
                .isProgressAnalysisAvailable(IngestorCommonTransformer.convertStringToBoolean(
                        form.getTestAnalysisAndComparisonReportAvailable()))
                .isRankAnalysisAvailable(IngestorCommonTransformer.convertStringToBoolean(
                        form.getAllIndiaRankAvailable()))
                .testCount(form.getNumberOfTestsInTestSeries())
                .testDuration(form.getDurationOfEachTestInTestSeries())
                .testQuestionCount(form.getNumberOfQuestionsInTestSeries())
                .testPracticePaperCount(form.getNumberOfDailyPracticeTestsAvailableInTestSeries())
                .distanceLearningBooksCount(form.getNumberOfBooks())
                .distanceLearningSolvedPaperCount(form.getNumberOfSolvedPapers())
                .distanceLearningAssignmentsCount(form.getNumberOfAssignments())
                .elearningLectureCount(form.getNumberOfLiveLectures())
                .elearningLectureDuration(form.getDurationOfEachLiveLecture())
                .elearningOnlineTestCount(form.getNumberOfTestsAvailableOnline())
                .elearningPracticePaperCount(form.getNumberOfDailyPracticePaperAvailableOnline())
                .classroomLectureCount(form.getNumberOfClassroomLectures())
                .classroomLectureDuration(form.getDurationOfClassroomLectures())
                .classroomTestCount(form.getNumberOfClassroomTests())
                .classroomTeacherStudentRatio(form.getTeacherToStudentRatio())
                .howToUse1(form.getHowToUse1())
                .howToUse2(form.getHowToUse2())
                .howToUse3(form.getHowToUse3())
                .howToUse4(form.getHowToUse4())
                .isEnabled(IngestorCommonTransformer.convertStringToBoolean(form.getStatusActive()))
                .build();
    }
}
