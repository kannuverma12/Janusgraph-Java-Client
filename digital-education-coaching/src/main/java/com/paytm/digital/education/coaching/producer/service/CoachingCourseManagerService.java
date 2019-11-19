package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.coaching.producer.model.dto.CoachingCourseDTO;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCourseDataRequest;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCoursePatchRequest;
import com.paytm.digital.education.database.entity.CoachingInstituteEntity;
import com.paytm.digital.education.exception.InvalidRequestException;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class CoachingCourseManagerService {

    private static final Logger log = LoggerFactory.getLogger(CoachingCourseManagerService.class);

    @Autowired
    private ProducerCoachingCourseService programService;

    @Autowired
    private ProducerCoachingInstituteService producerCoachingInstituteService;

    @Autowired
    private ProducerStreamService producerStreamService;

    @Autowired
    private ProducerTargetExamService producerTargetExamService;

    @Autowired
    private ProducerCoachingCourseFeatureService producerCoachingCourseFeatureService;


    public CoachingCourseDTO save(CoachingCourseDataRequest coachingCourseDataRequest) {
        if (Objects.nonNull(coachingCourseDataRequest.getCourseId())) {
            throw new InvalidRequestException(
                    "request should not have id : " + coachingCourseDataRequest.getCourseId());
        }

        CoachingInstituteEntity existingCoachingInstitutes =
                producerCoachingInstituteService
                        .findByInstituteId(coachingCourseDataRequest.getInstituteId());
        if (Objects.isNull(existingCoachingInstitutes)) {
            throw new InvalidRequestException(
                    "institute id not present : " + coachingCourseDataRequest.getInstituteId());
        }
        producerStreamService.isValidStreamIds(coachingCourseDataRequest.getStreamIds());
        producerTargetExamService.isValidExamIds(coachingCourseDataRequest.getPrimaryExamIds());
        producerCoachingCourseFeatureService
                .isValidCourseFeatureIds(coachingCourseDataRequest.getCourseFeatureIds());
        return CoachingCourseDTO
                .builder().courseId(programService.save(coachingCourseDataRequest).getCourseId())
                .build();
    }

    public CoachingCourseDTO update(CoachingCourseDataRequest coachingCourseDataRequest) {

        Optional.ofNullable(coachingCourseDataRequest.getCourseId())
                .orElseThrow(() -> new InvalidRequestException("course id should be present"));

        CoachingInstituteEntity existingCoachingInstitutes =
                producerCoachingInstituteService
                        .findByInstituteId(coachingCourseDataRequest.getInstituteId());
        if (Objects.isNull(existingCoachingInstitutes)) {
            throw new InvalidRequestException(
                    "institute id not present" + coachingCourseDataRequest.getInstituteId());
        }
        producerStreamService.isValidStreamIds(coachingCourseDataRequest.getStreamIds());
        producerTargetExamService.isValidExamIds(coachingCourseDataRequest.getPrimaryExamIds());
        producerCoachingCourseFeatureService
                .isValidCourseFeatureIds(coachingCourseDataRequest.getCourseFeatureIds());
        return CoachingCourseDTO
                .builder()
                .courseId(programService.update(coachingCourseDataRequest).getCourseId()).build();
    }

    public CoachingCourseDTO patch(CoachingCoursePatchRequest coachingCoursePatchRequest) {

        Optional.ofNullable(coachingCoursePatchRequest.getCourseId())
                .orElseThrow(() -> new InvalidRequestException("course id should be present"));

        return CoachingCourseDTO
                .builder()
                .courseId(programService.patch(coachingCoursePatchRequest).getCourseId()).build();
    }
}
