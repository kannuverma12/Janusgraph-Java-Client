package com.paytm.digital.education.coaching.producer.transformer;

import com.paytm.digital.education.coaching.producer.model.request.CoachingCourseCreateRequest;
import com.paytm.digital.education.database.embedded.CoachingCourseSessionDetails;
import com.paytm.digital.education.database.entity.CoachingCourseEntity;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseTransformer {
    public CoachingCourseEntity transformProgramCreateRequestToProgramEntity(
            CoachingCourseCreateRequest coachingCourseCreateRequest,
            CoachingCourseEntity coachingCourseEntity) {
        return CoachingCourseEntity.builder()
                .name(ObjectUtils.defaultIfNull(coachingCourseCreateRequest.getName(),
                        coachingCourseEntity.getName()))
                .coachingInstituteId(ObjectUtils.defaultIfNull(
                        coachingCourseCreateRequest.getCoachingInstituteId(),
                        coachingCourseEntity.getCoachingInstituteId()))
                .courseType(ObjectUtils.defaultIfNull(
                        coachingCourseCreateRequest.getCourseType(),
                        coachingCourseEntity.getCourseType()))
                .stream(ObjectUtils.defaultIfNull(
                        coachingCourseCreateRequest.getStream(),
                        coachingCourseEntity.getStream()))
                .primaryExamIds(ObjectUtils.defaultIfNull(
                        coachingCourseCreateRequest.getPrimaryExamIds(),
                        coachingCourseEntity.getPrimaryExamIds()))
                .auxiliaryExamIds(ObjectUtils.defaultIfNull(
                        coachingCourseCreateRequest.getAuxiliaryExams(),
                        coachingCourseEntity.getAuxiliaryExamIds()))
                .duration(ObjectUtils.defaultIfNull(
                        coachingCourseCreateRequest.getDuration(),
                        coachingCourseEntity.getDuration()))
                .eligibility(ObjectUtils.defaultIfNull(
                        coachingCourseCreateRequest.getEligibility(),
                        coachingCourseEntity.getEligibility()))
                .info(ObjectUtils.defaultIfNull(
                        coachingCourseCreateRequest.getInfo(),
                        coachingCourseEntity.getInfo()))
                .description(ObjectUtils.defaultIfNull(
                        coachingCourseCreateRequest.getDescription(),
                        coachingCourseEntity.getDescription()))
                .price(ObjectUtils.defaultIfNull(
                        coachingCourseCreateRequest.getPrice(),
                        coachingCourseEntity.getPrice()))
                .level(ObjectUtils.defaultIfNull(
                        coachingCourseCreateRequest.getLevel(),
                        coachingCourseEntity.getLevel()))
                .language(ObjectUtils.defaultIfNull(
                        coachingCourseCreateRequest.getLanguage(),
                        coachingCourseEntity.getLanguage()))
                .syllabus(ObjectUtils.defaultIfNull(
                        coachingCourseCreateRequest.getSyllabusAndBrochure(),
                        coachingCourseEntity.getSyllabus()))
                .globalPriority(ObjectUtils.defaultIfNull(
                        coachingCourseCreateRequest.getGlobalPriority(),
                        coachingCourseEntity.getGlobalPriority()))
                .sessionDetails(ObjectUtils.defaultIfNull(
                        this.convert(coachingCourseCreateRequest.getSessionDetails()),
                        coachingCourseEntity.getSessionDetails()))
                .features(ObjectUtils.defaultIfNull(
                        coachingCourseCreateRequest.getFeatures(),
                        coachingCourseEntity.getFeatures()))
                .isScholarshipAvailable(ObjectUtils.defaultIfNull(
                        coachingCourseCreateRequest.getIsScholarshipAvailable(),
                        coachingCourseEntity.getIsScholarshipAvailable()))
                .testCount(ObjectUtils.defaultIfNull(
                        coachingCourseCreateRequest.getTestCount(),
                        coachingCourseEntity.getTestCount()))
                .testDuration(ObjectUtils.defaultIfNull(
                        coachingCourseCreateRequest.getTestDuration(),
                        coachingCourseEntity.getTestDuration()))
                .testSeriesDuration(ObjectUtils.defaultIfNull(
                        coachingCourseCreateRequest.getTestSeriesDuration(),
                        coachingCourseEntity.getTestSeriesDuration()))
                .typesOfResults(ObjectUtils.defaultIfNull(
                        coachingCourseCreateRequest.getTypesOfResults(),
                        coachingCourseEntity.getTypesOfResults()))
                .isDoubtSolvingSessionAvailable(ObjectUtils.defaultIfNull(
                        coachingCourseCreateRequest.getIsDoubtSolvingSessionAvailable(),
                        coachingCourseEntity.getIsDoubtSolvingSessionAvailable()))
                .numberOfBooks(ObjectUtils.defaultIfNull(
                        coachingCourseCreateRequest.getNumberOfBooks(),
                        coachingCourseEntity.getNumberOfBooks()))
                .deliverySchedule(ObjectUtils.defaultIfNull(
                        coachingCourseCreateRequest.getDeliverySchedule(),
                        coachingCourseEntity.getDeliverySchedule()))
                .inclusions(ObjectUtils.defaultIfNull(
                        coachingCourseCreateRequest.getInclusions(),
                        coachingCourseEntity.getInclusions()))
                .howToUse(ObjectUtils.defaultIfNull(
                        coachingCourseCreateRequest.getHowToUse(),
                        coachingCourseEntity.getHowToUse()))
                .isEnabled(ObjectUtils.defaultIfNull(
                        coachingCourseCreateRequest.getIsEnabled(),
                        coachingCourseEntity.getIsEnabled()))
                .build();
    }

    private List<CoachingCourseSessionDetails> convert(
            List<com.paytm.digital.education.coaching.producer.model.dto.CoachingProgramSessionDetails> list) {

        return list.stream()
                .map(detail -> CoachingCourseSessionDetails.builder()
                        .key(detail.getKey())
                        .value(detail.getValue())
                        .priority(detail.getPriority())
                        .build())
                .collect(Collectors.toList());
    }
}
