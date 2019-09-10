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
    private CoachingInstituteService coachingInstituteService;

    @Autowired
    private StreamService streamService;

    @Autowired
    private TargetExamService targetExamService;

    public CoachingInstituteDTO create(CoachingInstituteDataRequest request) {
        if (Objects.nonNull(request.getInstituteId())) {
            throw new InvalidRequestException(
                    "request should not have id : " + request.getInstituteId());
        }
        streamService.isValidStreamIds(request.getStreamIds());
        targetExamService.isValidExamIds(request.getExamIds());

        return CoachingInstituteDTO.builder()
                .instituteId(coachingInstituteService.create(request).getInstituteId()).build();
    }

    public CoachingInstituteDTO update(CoachingInstituteDataRequest request) {
        Optional.ofNullable(request.getInstituteId())
                .orElseThrow(() -> new InvalidRequestException("institute id should be present"));
        streamService.isValidStreamIds(request.getStreamIds());
        targetExamService.isValidExamIds(request.getExamIds());
        return CoachingInstituteDTO.builder()
                .instituteId(coachingInstituteService.update(request).getInstituteId()).build();
    }
}
