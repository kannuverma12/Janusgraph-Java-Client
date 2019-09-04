package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.coaching.producer.model.dto.CoachingCourseFeatureDTO;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCourseFeatureDataRequest;
import com.paytm.digital.education.database.entity.CoachingInstituteEntity;
import com.paytm.digital.education.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
                    "Coaching Course feature ID should be null in post request");
        }
        CoachingInstituteEntity existingInstitute =
                Optional.ofNullable(
                        coachingInstituteService.findByInstituteId(request.getInstituteId()))
                        .orElseThrow(() -> new InvalidRequestException("institute not present"));
        return CoachingCourseFeatureDTO.builder()
                .coachingCourseFeatureId(
                        coachingCourseFeatureService.create(request).getCoachingCourseFeatureId())
                .build();
    }

    public CoachingCourseFeatureDTO update(CoachingCourseFeatureDataRequest request) {
        CoachingInstituteEntity existingInstitute =
                Optional.ofNullable(
                        coachingInstituteService.findByInstituteId(request.getInstituteId()))
                        .orElseThrow(() -> new InvalidRequestException("institute not present"));
        return CoachingCourseFeatureDTO.builder()
                .coachingCourseFeatureId(
                        coachingCourseFeatureService.update(request).getCoachingCourseFeatureId()
                )
                .build();
    }
}
