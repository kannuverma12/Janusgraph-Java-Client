package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.coaching.producer.model.dto.CoachingInstituteDTO;
import com.paytm.digital.education.coaching.producer.model.request.CoachingInstituteDataRequest;
import com.paytm.digital.education.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class CoachingInstituteManagerService {

    @Autowired
    private ProducerCoachingInstituteService producerCoachingInstituteService;

    @Autowired
    private ProducerStreamService producerStreamService;

    @Autowired
    private ProducerTargetExamService producerTargetExamService;

    public CoachingInstituteDTO create(CoachingInstituteDataRequest request) {
        if (Objects.nonNull(request.getInstituteId())) {
            throw new InvalidRequestException(
                    "request should not have id : " + request.getInstituteId());
        }
        producerStreamService.isValidStreamIds(request.getStreamIds());
        producerTargetExamService.isValidExamIds(request.getExamIds());

        return CoachingInstituteDTO.builder()
                .instituteId(producerCoachingInstituteService.create(request).getInstituteId()).build();
    }

    public CoachingInstituteDTO update(CoachingInstituteDataRequest request) {
        Optional.ofNullable(request.getInstituteId())
                .orElseThrow(() -> new InvalidRequestException("institute id should be present"));
        producerStreamService.isValidStreamIds(request.getStreamIds());
        producerTargetExamService.isValidExamIds(request.getExamIds());
        return CoachingInstituteDTO.builder()
                .instituteId(producerCoachingInstituteService.update(request).getInstituteId()).build();
    }
}
