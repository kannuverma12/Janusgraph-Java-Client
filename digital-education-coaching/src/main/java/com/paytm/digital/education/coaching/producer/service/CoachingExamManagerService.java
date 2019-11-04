package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.coaching.producer.model.dto.CoachingExamDTO;
import com.paytm.digital.education.coaching.producer.model.request.CoachingExamDataRequest;
import com.paytm.digital.education.database.entity.CoachingInstituteEntity;
import com.paytm.digital.education.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class CoachingExamManagerService {

    @Autowired
    private ProducerCoachingExamService coachingExamService;

    @Autowired
    private ProducerCoachingInstituteService producerCoachingInstituteService;

    @Autowired
    private ProducerStreamService producerStreamService;

    @Autowired
    private ProducerCoachingCourseService producerCoachingCourseService;

    public CoachingExamDTO insertCoachingExam(CoachingExamDataRequest request) {
        if (Objects.nonNull(request.getCoachingExamId())) {
            throw new InvalidRequestException(
                    "request should not have id : " + request.getCoachingExamId());
        }

        CoachingInstituteEntity existingCoachingInstitute =
                producerCoachingInstituteService.findByInstituteId(request.getInstituteId());
        if (Objects.isNull(existingCoachingInstitute)) {
            throw new InvalidRequestException(
                    "institute id not present : " + request.getInstituteId());
        }
        producerCoachingCourseService.isValidCourseIds(request.getCourseIds());
        producerStreamService.isValidStreamIds(request.getStreamIds());
        return CoachingExamDTO.builder()
                .coachingExamId(coachingExamService.insertCoachingExam(request).getCoachingExamId())
                .build();
    }

    public CoachingExamDTO updateCoachingExam(CoachingExamDataRequest request) {
        Optional.ofNullable(request.getCoachingExamId())
                .orElseThrow(() -> new InvalidRequestException(
                        "coaching exams id should be present"));

        CoachingInstituteEntity existingCoachingInstitutes =
                producerCoachingInstituteService.findByInstituteId(request.getInstituteId());
        if (Objects.isNull(existingCoachingInstitutes)) {
            throw new InvalidRequestException(
                    "institute id not present" + request.getInstituteId());
        }
        producerCoachingCourseService.isValidCourseIds(request.getCourseIds());
        producerStreamService.isValidStreamIds(request.getStreamIds());
        return CoachingExamDTO.builder()
                .coachingExamId(coachingExamService.updateCoachingExam(request).getCoachingExamId())
                .build();
    }
}
