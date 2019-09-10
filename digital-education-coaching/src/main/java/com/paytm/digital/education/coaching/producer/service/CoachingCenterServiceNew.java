package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.coaching.db.dao.CoachingCenterDAO;
import com.paytm.digital.education.coaching.producer.ConverterUtil;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCenterDataRequest;
import com.paytm.digital.education.database.entity.CoachingCenterEntity;
import com.paytm.digital.education.exception.InvalidRequestException;
import com.paytm.digital.education.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CoachingCenterServiceNew {

    @Autowired
    private CoachingCenterDAO coachingCenterDAO;

    public CoachingCenterEntity insertCoachingCenter(CoachingCenterDataRequest request) {

        CoachingCenterEntity coachingCenterEntity = new CoachingCenterEntity();
        ConverterUtil.setCoachingCenter(request, coachingCenterEntity);
        try {
            return coachingCenterDAO.save(coachingCenterEntity);
        } catch (DataIntegrityViolationException ex) {
            throw new InvalidRequestException(ex.getMessage(), ex);
        }
    }

    public CoachingCenterEntity updateCoachingCenter(CoachingCenterDataRequest request) {
        CoachingCenterEntity existingCenter =
                Optional.ofNullable(coachingCenterDAO.findByCenterId(request.getCenterId()))
                        .orElseThrow(() -> new InvalidRequestException("coaching center not present"));
        ConverterUtil.setCoachingCenter(request, existingCenter);
        try {
            return coachingCenterDAO.save(existingCenter);
        } catch (DataIntegrityViolationException ex) {
            throw new InvalidRequestException(ex.getMessage(), ex);
        }
    }
}
