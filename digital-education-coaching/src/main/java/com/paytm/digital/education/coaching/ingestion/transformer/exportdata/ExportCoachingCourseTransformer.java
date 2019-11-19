package com.paytm.digital.education.coaching.ingestion.transformer.exportdata;

import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingCourseForm;
import com.paytm.digital.education.database.embedded.CoachingCourseImportantDate;
import com.paytm.digital.education.database.entity.CoachingCourseEntity;
import com.paytm.digital.education.enums.CTAViewType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.EMPTY_STRING;

public class ExportCoachingCourseTransformer {

    public static List<CoachingCourseForm> convert(final List<CoachingCourseEntity> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return new ArrayList<>();
        }

        return entityList.stream()
                .map(entity -> {
                    CoachingCourseForm form = CoachingCourseForm.builder()
                            .courseId(entity.getCourseId())
                            .courseName(entity.getName())
                            .instituteId(entity.getCoachingInstituteId())
                            .courseType(entity.getCourseType().getText())
                            .streamIds(entity.getStreamIds() == null
                                    ? EMPTY_STRING : StringUtils.join(entity.getStreamIds(), ","))
                            .examPreparedIds(entity.getPrimaryExamIds() == null
                                    ? EMPTY_STRING :
                                    StringUtils.join(entity.getPrimaryExamIds(), ","))
                            .courseDurationValue(entity.getDuration())
                            .courseDurationType(entity.getDurationType().getText())
                            .courseValidityValue(entity.getValidity())
                            .eligibilityCriteria(entity.getEligibility())
                            .courseIntroduction(entity.getInfo())
                            .courseDescription(entity.getDescription())
                            .featureIds(entity.getCourseFeatureIds() == null
                                    ? EMPTY_STRING :
                                    StringUtils.join(entity.getCourseFeatureIds(), ","))
                            .originalPrice(entity.getOriginalPrice())
                            .discountedPrice(entity.getDiscountedPrice())
                            .levelOfEducation(entity.getCourseLevel().getDisplayName())
                            .language(entity.getLanguage().getText())
                            .syllabus(entity.getSyllabus())
                            .courseCovers(entity.getCourseCover().getText())
                            .howToUse1(entity.getHowToUse1())
                            .howToUse2(entity.getHowToUse2())
                            .howToUse3(entity.getHowToUse3())
                            .howToUse4(entity.getHowToUse4())
                            .certificate(ExportCommonTransformer.convertBooleanToString(
                                    entity.getIsCertificateAvailable()))
                            .doubtSolvingSessionAvailable(
                                    ExportCommonTransformer.convertBooleanToString(
                                            entity.getIsDoubtSolvingForumAvailable()))
                            .testAnalysisAndComparisonReportAvailable(
                                    ExportCommonTransformer.convertBooleanToString(
                                            entity.getIsProgressAnalysisAvailable()))
                            .allIndiaRankAvailable(ExportCommonTransformer.convertBooleanToString(
                                    entity.getIsRankAnalysisAvailable()))
                            .numberOfTestsInTestSeries(entity.getTestCount())
                            .durationOfEachTestInTestSeries(entity.getTestDuration())
                            .numberOfQuestionsInTestSeries(entity.getTestQuestionCount())
                            .numberOfDailyPracticeTestsAvailableInTestSeries(
                                    entity.getTestPracticePaperCount())
                            .numberOfBooks(entity.getDistanceLearningBooksCount())
                            .numberOfSolvedPapers(entity.getDistanceLearningSolvedPaperCount())
                            .numberOfAssignments(entity.getDistanceLearningAssignmentsCount())
                            .numberOfLiveLectures(entity.getElearningLectureCount())
                            .durationOfEachLiveLecture(entity.getElearningLectureCount())
                            .numberOfTestsAvailableOnline(entity.getElearningOnlineTestCount())
                            .numberOfDailyPracticePaperAvailableOnline(
                                    entity.getElearningPracticePaperCount())
                            .numberOfClassroomLectures(entity.getClassroomLectureCount())
                            .durationOfClassroomLectures(entity.getClassroomLectureDuration())
                            .numberOfClassroomTests(entity.getClassroomTestCount())
                            .teacherToStudentRatio(entity.getClassroomTeacherStudentRatio())
                            .merchantProductId(entity.getMerchantProductId())
                            .sgst(entity.getSgst())
                            .cgst(entity.getCgst())
                            .igst(entity.getIgst())
                            .tcs(entity.getTcs())
                            .globalPriority(entity.getPriority())
                            .statusActive(ExportCommonTransformer.convertBooleanToString(
                                    entity.getIsEnabled()))
                            .paytmProductId(entity.getPaytmProductId())
                            .isDynamic(ExportCommonTransformer
                                    .convertBooleanToString(entity.getIsDynamic()))
                            .build();

                    if (Objects.nonNull(entity.getValidityType())) {
                        form.setCourseValidityType(entity.getValidityType().getText());
                    }
                    fillImportantDates(form, entity.getImportantDates());
                    return form;
                })
                .collect(Collectors.toList());
    }

    private static void fillImportantDates(final CoachingCourseForm form,
            final List<CoachingCourseImportantDate> importantDates) {
        if (CollectionUtils.isEmpty(importantDates)) {
            return;
        }
        if (null != importantDates.get(0)) {
            form.setImportantDateKey1(importantDates.get(0).getKey());
            form.setImportantDateVal1(importantDates.get(0).getValue());
        }
        if (importantDates.size() > 1 && null != importantDates.get(1)) {
            form.setImportantDateKey2(importantDates.get(1).getKey());
            form.setImportantDateVal2(importantDates.get(1).getValue());
        }
        if (importantDates.size() > 2 && null != importantDates.get(2)) {
            form.setImportantDateKey3(importantDates.get(2).getKey());
            form.setImportantDateVal3(importantDates.get(2).getValue());
        }
    }
}
