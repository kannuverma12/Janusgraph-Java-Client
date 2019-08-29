package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.coaching.db.dao.CoachingExamDAO;
import com.paytm.digital.education.coaching.producer.ConverterUtil;
import com.paytm.digital.education.coaching.producer.model.request.CoachingExamDataRequest;
import com.paytm.digital.education.database.entity.CoachingExamEntity;
import com.paytm.digital.education.exception.InvalidRequestException;
import com.paytm.digital.education.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CoachingExamServiceNew {

    private static String          data = "coaching exam not present";
    @Autowired
    private        CoachingExamDAO coachingExamDAO;

    public CoachingExamEntity insertCoachingExam(CoachingExamDataRequest request) {

        CoachingExamEntity coachingExamEntity = new CoachingExamEntity();
        ConverterUtil.setCoachingExam(request, coachingExamEntity);
        try {
            return coachingExamDAO.save(coachingExamEntity);
        } catch (DataIntegrityViolationException ex) {
            throw new InvalidRequestException(ex.getMessage(), ex);

        }
    }

    public CoachingExamEntity updateCoachingExam(CoachingExamDataRequest request) {
        CoachingExamEntity existingCoachingExam =
                Optional.ofNullable(coachingExamDAO.findByExamId(request.getCoachingExamId()))
                        .orElseThrow(() -> new ResourceNotFoundException(data));
        ConverterUtil.setCoachingExam(request, existingCoachingExam);
        return coachingExamDAO.save(existingCoachingExam);
    }
}
