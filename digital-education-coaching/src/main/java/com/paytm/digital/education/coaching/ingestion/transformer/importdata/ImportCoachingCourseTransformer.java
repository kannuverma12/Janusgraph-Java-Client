package com.paytm.digital.education.coaching.ingestion.transformer.importdata;

import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingCourseForm;
import com.paytm.digital.education.coaching.producer.model.embedded.ImportantDate;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCourseDataRequest;
import com.paytm.digital.education.enums.CourseCover;
import com.paytm.digital.education.enums.CourseLevel;
import com.paytm.digital.education.enums.CourseType;
import com.paytm.digital.education.enums.DurationType;
import com.paytm.digital.education.enums.Language;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ImportCoachingCourseTransformer {

    public static CoachingCourseDataRequest convert(final CoachingCourseForm form) {
        if (null == form) {
            return null;
        }
        return CoachingCourseDataRequest.builder()
                .courseId(form.getCourseId())
                .name(form.getCourseName())
                .instituteId(form.getInstituteId())
                .courseType(CourseType.fromString(form.getCourseType()))
                .streamIds(ImportCommonTransformer.convertStringToListOfLong(
                        form.getStreamIds()))
                .primaryExamIds(ImportCommonTransformer.convertStringToListOfLong(
                        form.getExamPreparedIds()))
                .duration(form.getCourseDurationValue())
                .durationType(DurationType.fromString(form.getCourseDurationType()))
                .eligibility(form.getEligibilityCriteria())
                .info(form.getCourseIntroduction())
                .description(form.getCourseDescription())
                .courseFeatureIds(ImportCommonTransformer.convertStringToListOfLong(
                        form.getFeatureIds()))
                .originalPrice(form.getOriginalPrice())
                .discountedPrice(form.getDiscountedPrice())
                .courseLevel(CourseLevel.fromString(form.getLevelOfEducation()))
                .language(Language.fromString(form.getLanguage()))
                .syllabusAndBrochure(form.getSyllabus())
                .isCertificateAvailable(ImportCommonTransformer.convertStringToBoolean(
                        form.getCertificate()))
                .isDoubtSolvingForumAvailable(ImportCommonTransformer.convertStringToBoolean(
                        form.getDoubtSolvingSessionAvailable()))
                .isProgressAnalysisAvailable(ImportCommonTransformer.convertStringToBoolean(
                        form.getTestAnalysisAndComparisonReportAvailable()))
                .isRankAnalysisAvailable(ImportCommonTransformer.convertStringToBoolean(
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
                .merchantProductId(form.getMerchantProductId())
                .importantDates(buildImpDates(form))
                .courseCover(CourseCover.fromString(form.getCourseCovers()))
                .sgst(form.getSgst())
                .cgst(form.getCgst())
                .igst(form.getIgst())
                .tcs(form.getTcs())
                .priority(form.getGlobalPriority())
                .isEnabled(ImportCommonTransformer.convertStringToBoolean(form.getStatusActive()))
                .paytmProductId(form.getPaytmProductId())
                .isDynamic(ImportCommonTransformer.convertStringToBoolean(form.getIsDynamic()))
                .redirectUrl(form.getRedirectUrl())
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
