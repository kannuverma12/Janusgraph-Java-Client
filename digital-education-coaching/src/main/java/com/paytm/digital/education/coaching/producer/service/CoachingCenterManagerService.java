package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.coaching.producer.model.dto.CoachingCenterDTO;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCenterDataRequest;
import com.paytm.digital.education.database.entity.CoachingInstituteEntity;
import com.paytm.digital.education.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class CoachingCenterManagerService {

    @Autowired
    private ProducerCoachingCenterService coachingCenterService;

    @Autowired
    private ProducerCoachingInstituteService producerCoachingInstituteService;

    public CoachingCenterDTO insertCoachingCenter(CoachingCenterDataRequest request) {

        if (Objects.nonNull(request.getCenterId())) {
            throw new InvalidRequestException(
                    "request should not have id : " + request.getCenterId());
        }

        CoachingInstituteEntity existingCoachingInstitutes =
                producerCoachingInstituteService.findByInstituteId(request.getInstituteId());
        if (Objects.isNull(existingCoachingInstitutes)) {
            throw new InvalidRequestException(
                    "institute id not present : " + request.getInstituteId());
        }

        return CoachingCenterDTO.builder()
                .centerId(coachingCenterService.insertCoachingCenter(request).getCenterId())
                .build();
    }

    public CoachingCenterDTO updateCoachingCenter(CoachingCenterDataRequest request) {

        Optional.ofNullable(request.getCenterId())
                .orElseThrow(() -> new InvalidRequestException("center id should be present"));

        CoachingInstituteEntity existingCoachingInstitutes =
                producerCoachingInstituteService.findByInstituteId(request.getInstituteId());
        if (Objects.isNull(existingCoachingInstitutes)) {
            throw new InvalidRequestException(
                    "institute id not present : " + request.getInstituteId());
        }

        return CoachingCenterDTO.builder()
                .centerId(coachingCenterService.updateCoachingCenter(request).getCenterId())
                .build();
    }
}
