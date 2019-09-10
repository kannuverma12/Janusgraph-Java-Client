package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.coaching.producer.model.dto.CoachingCourseFeatureDTO;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCourseFeatureDataRequest;
import com.paytm.digital.education.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Objects;
import java.util.Optional;

@Service
public class CoachingCourseFeatureManagerService {

    @Autowired
    private CoachingCourseFeatureService coachingCourseFeatureService;

    @Autowired
    private CoachingInstituteService coachingInstituteService;

    public CoachingCourseFeatureDTO create(CoachingCourseFeatureDataRequest request) {
        if (Objects.nonNull(request.getCoachingCourseFeatureId())) {
            throw new InvalidRequestException(
                    "coaching course feature id should be null in post request");
        }
        Optional.ofNullable(
                coachingInstituteService.findByInstituteId(request.getInstituteId()))
                .orElseThrow(() -> new InvalidRequestException("institute id not present"));
        if (!CollectionUtils.isEmpty(coachingCourseFeatureService
                .findByInstituteIdAndName(request.getInstituteId(),
                        request.getCoachingCourseFeatureName().getText()))) {
            throw new InvalidRequestException(
                    "Feature name in specified institute already exists");
        }
        return CoachingCourseFeatureDTO.builder()
                .coachingCourseFeatureId(
                        coachingCourseFeatureService.create(request).getCoachingCourseFeatureId())
                .build();
    }

    public CoachingCourseFeatureDTO update(CoachingCourseFeatureDataRequest request) {

        Optional.ofNullable(request.getCoachingCourseFeatureId())
                .orElseThrow(() -> new InvalidRequestException("feature id should be present"));

        Optional.ofNullable(
                coachingInstituteService.findByInstituteId(request.getInstituteId()))
                .orElseThrow(() -> new InvalidRequestException("institute id not present"));
        if (!CollectionUtils.isEmpty(coachingCourseFeatureService
                .findByInstituteIdAndName(request.getInstituteId(),
                        request.getCoachingCourseFeatureName().getText()))) {
            throw new InvalidRequestException(
                    "Feature name in specified institute already exists");
        }
        return CoachingCourseFeatureDTO.builder()
                .coachingCourseFeatureId(
                        coachingCourseFeatureService.update(request).getCoachingCourseFeatureId()
                )
                .build();
    }
}
