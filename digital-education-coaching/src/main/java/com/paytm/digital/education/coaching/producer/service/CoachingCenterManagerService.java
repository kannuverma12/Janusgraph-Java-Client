package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.coaching.producer.model.dto.CoachingCenterDTO;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCenterDataRequest;
import com.paytm.digital.education.database.entity.CoachingInstituteEntity;
import com.paytm.digital.education.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CoachingCenterManagerService {

    @Autowired
    private CoachingCenterServiceNew coachingCenterService;

    @Autowired
    private CoachingInstituteService coachingInstituteService;

    public CoachingCenterDTO insertCoachingCenter(CoachingCenterDataRequest request) {


        CoachingInstituteEntity existingCoachingInstitutes =
                coachingInstituteService.findByInstituteId(request.getInstituteId());
        if (Objects.isNull(existingCoachingInstitutes)) {
            throw new InvalidRequestException("coaching institute not present");
        }

        return CoachingCenterDTO.builder()
                .centerId(coachingCenterService.insertCoachingCenter(request).getCenterId())
                .build();
    }

    public CoachingCenterDTO updateCoachingCenter(CoachingCenterDataRequest request) {

        CoachingInstituteEntity existingCoachingInstitutes =
                coachingInstituteService.findByInstituteId(request.getInstituteId());
        if (Objects.isNull(existingCoachingInstitutes)) {
            throw new InvalidRequestException("coaching institute not present");
        }

        return CoachingCenterDTO.builder()
                .centerId(coachingCenterService.updateCoachingCenter(request).getCenterId())
                .build();
    }
}