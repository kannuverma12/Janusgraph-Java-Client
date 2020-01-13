package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.coaching.producer.model.dto.CoachingCourseFeatureDTO;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCourseFeatureDataRequest;
import com.paytm.digital.education.database.entity.CoachingCourseFeatureEntity;
import com.paytm.digital.education.database.entity.CoachingInstituteEntity;
import com.paytm.digital.education.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Objects;
import java.util.Optional;

@Service
public class CoachingCourseFeatureManagerService {

    @Autowired
    private ProducerCoachingCourseFeatureService producerCoachingCourseFeatureService;

    @Autowired
    private ProducerCoachingInstituteService producerCoachingInstituteService;

    public CoachingCourseFeatureDTO create(CoachingCourseFeatureDataRequest request) {
        if (Objects.nonNull(request.getCoachingCourseFeatureId())) {
            throw new InvalidRequestException(
                    "request should not have id : " + request.getCoachingCourseFeatureId());
        }
        Optional.ofNullable(
                producerCoachingInstituteService.findByInstituteId(request.getInstituteId()))
                .orElseThrow(() -> new InvalidRequestException(
                        "institute id not present : " + request.getInstituteId()));
        if (!CollectionUtils.isEmpty(producerCoachingCourseFeatureService
                .findByInstituteIdAndName(request.getInstituteId(),
                        request.getCoachingCourseFeatureName().getText()))) {
            throw new InvalidRequestException(
                    "Feature name in specified institute already exists : " + request
                            .getCoachingCourseFeatureName());
        }
        return CoachingCourseFeatureDTO.builder()
                .coachingCourseFeatureId(
                        producerCoachingCourseFeatureService.create(request).getCoachingCourseFeatureId())
                .build();
    }

    public CoachingCourseFeatureDTO update(CoachingCourseFeatureDataRequest request) {

        Optional.ofNullable(request.getCoachingCourseFeatureId())
                .orElseThrow(() -> new InvalidRequestException("feature id should be present"));

        Optional.ofNullable(
                producerCoachingInstituteService.findByInstituteId(request.getInstituteId()))
                .orElseThrow(() -> new InvalidRequestException(
                        "institute id not present : " + request.getInstituteId()));

        CoachingCourseFeatureEntity coachingCourseFeatureEntity =
                Optional.ofNullable(
                        producerCoachingCourseFeatureService
                                .findByCoachingCourseFeatureId(request.getCoachingCourseFeatureId()))
                        .orElseThrow(() -> new InvalidRequestException("feature id not present in db"));

        if (Objects.nonNull(coachingCourseFeatureEntity.getName())
                && !coachingCourseFeatureEntity.getName()
                .equals(request.getCoachingCourseFeatureName().getText())) {
            if (!CollectionUtils.isEmpty(producerCoachingCourseFeatureService
                    .findByInstituteIdAndName(request.getInstituteId(),
                            request.getCoachingCourseFeatureName().getText()))) {
                throw new InvalidRequestException(
                        "Feature name in specified institute already exists : " + request
                                .getCoachingCourseFeatureName());
            }
        }
        return CoachingCourseFeatureDTO.builder()
                .coachingCourseFeatureId(
                        producerCoachingCourseFeatureService.update(request)
                                .getCoachingCourseFeatureId()
                )
                .build();
    }
}
