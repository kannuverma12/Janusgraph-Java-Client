package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.coaching.constants.CoachingConstants;
import com.paytm.digital.education.coaching.db.dao.CoachingCourseDAO;
import com.paytm.digital.education.coaching.exeptions.ResourceNotPresentException;
import com.paytm.digital.education.coaching.producer.ConverterUtil;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCourseDataRequest;
import com.paytm.digital.education.database.entity.CoachingCourseEntity;
import com.paytm.digital.education.exception.InvalidRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class CoachingCourseAdminService {

    @Autowired
    private CoachingCourseDAO coachingCourseDAO;


    public CoachingCourseEntity save(CoachingCourseDataRequest coachingCourseDataRequest) {

        CoachingCourseEntity coachingCourseEntity = new CoachingCourseEntity();
        ConverterUtil.setCoachingCourse(coachingCourseDataRequest, coachingCourseEntity);

        try {
            return coachingCourseDAO.save(coachingCourseEntity);
        } catch (DataIntegrityViolationException ex) {
            throw new InvalidRequestException(ex.getMessage(), ex);
        }
    }

    public CoachingCourseEntity update(CoachingCourseDataRequest coachingCourseDataRequest) {
        CoachingCourseEntity coachingCourseEntity = Optional.ofNullable(
                coachingCourseDAO.findByProgramId(coachingCourseDataRequest.getCourseId()))
                .orElseThrow(() -> new ResourceNotPresentException(
                        CoachingConstants.RESOURCE_NOT_PRESENT));

        ConverterUtil.setCoachingCourse(coachingCourseDataRequest, coachingCourseEntity);
        return coachingCourseDAO.save(coachingCourseEntity);
    }
}
