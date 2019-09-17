package com.paytm.digital.education.coaching.ingestion.transformer;

import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingCourseForm;
import com.paytm.digital.education.coaching.producer.model.embedded.ImportantDate;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCourseDataRequest;
import com.paytm.digital.education.enums.CourseLevel;
import com.paytm.digital.education.enums.CourseType;
import com.paytm.digital.education.enums.DurationType;
import com.paytm.digital.education.enums.Language;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

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
                .importantDates(buildImpDates(form))
                .isEnabled(IngestorCommonTransformer.convertStringToBoolean(form.getStatusActive()))
                .build();
    }

    private static List<ImportantDate> buildImpDates(final CoachingCourseForm form) {
        final List<ImportantDate> importantDateList = new ArrayList<>();

        if (!StringUtils.isEmpty(form.getImportantDateKey1())
                && !StringUtils.isEmpty(form.getImportantDateVal1())) {
            importantDateList.add(new ImportantDate(form.getImportantDateKey1(),
                    form.getImportantDateVal1()));
        }
        if (!StringUtils.isEmpty(form.getImportantDateKey2())
                && !StringUtils.isEmpty(form.getImportantDateVal2())) {
            importantDateList.add(new ImportantDate(form.getImportantDateKey2(),
                    form.getImportantDateVal2()));
        }
        if (!StringUtils.isEmpty(form.getImportantDateKey3())
                && !StringUtils.isEmpty(form.getImportantDateVal3())) {
            importantDateList.add(new ImportantDate(form.getImportantDateKey3(),
                    form.getImportantDateVal3()));
        }

        return importantDateList;
    }
}
