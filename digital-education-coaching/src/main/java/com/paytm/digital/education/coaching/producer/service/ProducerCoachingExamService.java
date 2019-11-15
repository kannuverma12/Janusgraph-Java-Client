package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.database.dao.CoachingExamDAO;
import com.paytm.digital.education.coaching.producer.ConverterUtil;
import com.paytm.digital.education.coaching.producer.model.request.CoachingExamDataRequest;
import com.paytm.digital.education.database.entity.CoachingExamEntity;
import com.paytm.digital.education.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProducerCoachingExamService {

    @Autowired
    private CoachingExamDAO coachingExamDAO;

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
                        .orElseThrow(() -> new InvalidRequestException(
                                "exam id not present : " + request.getCoachingExamId()));
        ConverterUtil.setCoachingExam(request, existingCoachingExam);
        try {
            return coachingExamDAO.save(existingCoachingExam);
        } catch (DataIntegrityViolationException ex) {
            throw new InvalidRequestException(ex.getMessage(), ex);
        }
    }
}
