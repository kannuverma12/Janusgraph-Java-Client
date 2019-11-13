package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.coaching.db.dao.CoachingCourseDAO;
import com.paytm.digital.education.coaching.producer.ConverterUtil;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCourseDataRequest;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCoursePatchRequest;
import com.paytm.digital.education.database.entity.CoachingCourseEntity;
import com.paytm.digital.education.exception.InvalidRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProducerCoachingCourseService {

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
                .orElseThrow(() -> new InvalidRequestException(
                        "course id not present : " + coachingCourseDataRequest.getCourseId()));

        ConverterUtil.setCoachingCourse(coachingCourseDataRequest, coachingCourseEntity);
        try {
            return coachingCourseDAO.save(coachingCourseEntity);
        } catch (DataIntegrityViolationException ex) {
            throw new InvalidRequestException(ex.getMessage(), ex);
        }
    }

    public CoachingCourseEntity patch(CoachingCoursePatchRequest coachingCoursePatchRequest) {
        CoachingCourseEntity coachingCourseEntity = Optional.ofNullable(
                coachingCourseDAO.findByProgramId(coachingCoursePatchRequest.getCourseId()))
                .orElseThrow(() -> new InvalidRequestException(
                        "course id not present : " + coachingCoursePatchRequest.getCourseId()));
        ConverterUtil.patchCoachingCourse(coachingCoursePatchRequest, coachingCourseEntity);
        try {
            return coachingCourseDAO.save(coachingCourseEntity);
        } catch (NonTransientDataAccessException ex) {
            throw new InvalidRequestException(ex.getMessage(), ex);
        }
    }

    public boolean isValidCourseIds(List<Long> ids) {
        List<Long> existingCourseIds = coachingCourseDAO.findAllByCourseId(ids)
                .stream().map(CoachingCourseEntity::getCourseId).collect(Collectors.toList());
        List<Long> invalidCourseIds = ids.stream().filter(id -> !existingCourseIds.contains(id))
                .collect(Collectors.toList());
        if (!invalidCourseIds.isEmpty()) {
            throw new InvalidRequestException("invalid course ids given : " + invalidCourseIds);
        }
        return true;
    }
}
