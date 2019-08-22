package com.paytm.digital.education.coaching.producer.transformer;

import com.paytm.digital.education.coaching.producer.model.request.CoachingProgramCreateRequest;
import com.paytm.digital.education.database.embedded.CoachingProgramSessionDetails;
import com.paytm.digital.education.database.entity.CoachingProgramEntity;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProgramTransformer {
    public CoachingProgramEntity transformProgramCreateRequestToProgramEntity(
            CoachingProgramCreateRequest coachingProgramCreateRequest,
            CoachingProgramEntity coachingProgramEntity) {
        return CoachingProgramEntity.builder()
                .name(ObjectUtils.defaultIfNull(coachingProgramCreateRequest.getName(),
                        coachingProgramEntity.getName()))
                .coachingInstituteId(ObjectUtils.defaultIfNull(
                        coachingProgramCreateRequest.getCoachingInstituteId(),
                        coachingProgramEntity.getCoachingInstituteId()))
                .courseType(ObjectUtils.defaultIfNull(
                        coachingProgramCreateRequest.getCourseType(),
                        coachingProgramEntity.getCourseType()))
                .stream(ObjectUtils.defaultIfNull(
                        coachingProgramCreateRequest.getStream(),
                        coachingProgramEntity.getStream()))
                .primaryExamIds(ObjectUtils.defaultIfNull(
                        coachingProgramCreateRequest.getPrimaryExamIds(),
                        coachingProgramEntity.getPrimaryExamIds()))
                .auxiliaryExamIds(ObjectUtils.defaultIfNull(
                        coachingProgramCreateRequest.getAuxiliaryExams(),
                        coachingProgramEntity.getAuxiliaryExamIds()))
                .duration(ObjectUtils.defaultIfNull(
                        coachingProgramCreateRequest.getDuration(),
                        coachingProgramEntity.getDuration()))
                .eligibility(ObjectUtils.defaultIfNull(
                        coachingProgramCreateRequest.getEligibility(),
                        coachingProgramEntity.getEligibility()))
                .info(ObjectUtils.defaultIfNull(
                        coachingProgramCreateRequest.getInfo(),
                        coachingProgramEntity.getInfo()))
                .description(ObjectUtils.defaultIfNull(
                        coachingProgramCreateRequest.getDescription(),
                        coachingProgramEntity.getDescription()))
                .price(ObjectUtils.defaultIfNull(
                        coachingProgramCreateRequest.getPrice(),
                        coachingProgramEntity.getPrice()))
                .level(ObjectUtils.defaultIfNull(
                        coachingProgramCreateRequest.getLevel(),
                        coachingProgramEntity.getLevel()))
                .language(ObjectUtils.defaultIfNull(
                        coachingProgramCreateRequest.getLanguage(),
                        coachingProgramEntity.getLanguage()))
                .syllabusAndBrochure(ObjectUtils.defaultIfNull(
                        coachingProgramCreateRequest.getSyllabusAndBrochure(),
                        coachingProgramEntity.getSyllabusAndBrochure()))
                .globalPriority(ObjectUtils.defaultIfNull(
                        coachingProgramCreateRequest.getGlobalPriority(),
                        coachingProgramEntity.getGlobalPriority()))
                .sessionDetails(ObjectUtils.defaultIfNull(
                        this.convert(coachingProgramCreateRequest.getSessionDetails()),
                        coachingProgramEntity.getSessionDetails()))
                .features(ObjectUtils.defaultIfNull(
                        coachingProgramCreateRequest.getFeatures(),
                        coachingProgramEntity.getFeatures()))
                .isScholarshipAvailable(ObjectUtils.defaultIfNull(
                        coachingProgramCreateRequest.getIsScholarshipAvailable(),
                        coachingProgramEntity.getIsScholarshipAvailable()))
                .testCount(ObjectUtils.defaultIfNull(
                        coachingProgramCreateRequest.getTestCount(),
                        coachingProgramEntity.getTestCount()))
                .testDuration(ObjectUtils.defaultIfNull(
                        coachingProgramCreateRequest.getTestDuration(),
                        coachingProgramEntity.getTestDuration()))
                .testSeriesDuration(ObjectUtils.defaultIfNull(
                        coachingProgramCreateRequest.getTestSeriesDuration(),
                        coachingProgramEntity.getTestSeriesDuration()))
                .typesOfResults(ObjectUtils.defaultIfNull(
                        coachingProgramCreateRequest.getTypesOfResults(),
                        coachingProgramEntity.getTypesOfResults()))
                .isDoubtSolvingSessionAvailable(ObjectUtils.defaultIfNull(
                        coachingProgramCreateRequest.getIsDoubtSolvingSessionAvailable(),
                        coachingProgramEntity.getIsDoubtSolvingSessionAvailable()))
                .numberOfBooks(ObjectUtils.defaultIfNull(
                        coachingProgramCreateRequest.getNumberOfBooks(),
                        coachingProgramEntity.getNumberOfBooks()))
                .deliverySchedule(ObjectUtils.defaultIfNull(
                        coachingProgramCreateRequest.getDeliverySchedule(),
                        coachingProgramEntity.getDeliverySchedule()))
                .inclusions(ObjectUtils.defaultIfNull(
                        coachingProgramCreateRequest.getInclusions(),
                        coachingProgramEntity.getInclusions()))
                .howToUse(ObjectUtils.defaultIfNull(
                        coachingProgramCreateRequest.getHowToUse(),
                        coachingProgramEntity.getHowToUse()))
                .isEnabled(ObjectUtils.defaultIfNull(
                        coachingProgramCreateRequest.getIsEnabled(),
                        coachingProgramEntity.getIsEnabled()))
                .build();
    }

    private List<CoachingProgramSessionDetails> convert(
            List<com.paytm.digital.education.coaching.producer.model.dto.CoachingProgramSessionDetails> list) {

        return list.stream()
                .map(detail -> CoachingProgramSessionDetails.builder()
                        .key(detail.getKey())
                        .value(detail.getValue())
                        .priority(detail.getPriority())
                        .build())
                .collect(Collectors.toList());
    }
}
