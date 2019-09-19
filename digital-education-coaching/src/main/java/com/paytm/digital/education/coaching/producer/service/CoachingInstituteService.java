package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.coaching.db.dao.CoachingInstituteDAO;
import com.paytm.digital.education.coaching.producer.ConverterUtil;
import com.paytm.digital.education.coaching.producer.model.request.CoachingInstituteDataRequest;
import com.paytm.digital.education.database.entity.CoachingInstituteEntity;
import com.paytm.digital.education.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CoachingInstituteService {

    @Autowired
    private CoachingInstituteDAO coachingInstituteDAO;

    public CoachingInstituteEntity create(CoachingInstituteDataRequest request) {

        CoachingInstituteEntity coachingInstituteEntity = new CoachingInstituteEntity();
        ConverterUtil.setCoachingInstituteData(request, coachingInstituteEntity);
        try {
            return coachingInstituteDAO.save(coachingInstituteEntity);
        } catch (DataIntegrityViolationException ex) {
            throw new InvalidRequestException(ex.getMessage(), ex);
        }
    }

    public CoachingInstituteEntity update(CoachingInstituteDataRequest request) {
        CoachingInstituteEntity existingIntitute =
                Optional.ofNullable(
                        coachingInstituteDAO.findByInstituteId(request.getInstituteId()))
                        .orElseThrow(() -> new InvalidRequestException(
                                "institute id not present : " + request.getInstituteId()));

        ConverterUtil.setCoachingInstituteData(request, existingIntitute);
        try {
            return coachingInstituteDAO.save(existingIntitute);
        } catch (DataIntegrityViolationException ex) {
            throw new InvalidRequestException(ex.getMessage(), ex);
        }
    }

    public List<CoachingInstituteEntity> findAllByInstituteId(List<Long> instituteIds) {
        return coachingInstituteDAO.findAllByInstituteId(instituteIds);
    }

    public CoachingInstituteEntity findByInstituteId(Long instituteId) {
        return coachingInstituteDAO.findByInstituteId(instituteId);
    }
}
