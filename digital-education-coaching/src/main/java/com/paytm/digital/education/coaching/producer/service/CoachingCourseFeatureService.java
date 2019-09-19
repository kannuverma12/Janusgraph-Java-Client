package com.paytm.digital.education.coaching.producer.service;

import com.paytm.digital.education.coaching.db.dao.CoachingCourseFeatureDAO;
import com.paytm.digital.education.coaching.producer.ConverterUtil;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCourseFeatureDataRequest;
import com.paytm.digital.education.database.entity.CoachingCourseFeatureEntity;
import com.paytm.digital.education.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CoachingCourseFeatureService {

    @Autowired
    private CoachingCourseFeatureDAO coachingCourseFeatureDAO;

    public CoachingCourseFeatureEntity create(CoachingCourseFeatureDataRequest request) {
        CoachingCourseFeatureEntity coachingCourseFeatureEntity = new CoachingCourseFeatureEntity();
        ConverterUtil.setCoachingCourseFeatureData(request, coachingCourseFeatureEntity);
        try {
            return coachingCourseFeatureDAO.save(coachingCourseFeatureEntity);
        } catch (DataIntegrityViolationException ex) {
            throw new InvalidRequestException(ex.getMessage(), ex);
        }
    }


    public CoachingCourseFeatureEntity update(CoachingCourseFeatureDataRequest request) {
        CoachingCourseFeatureEntity coachingCourseFeatureEntity = Optional.ofNullable(
                coachingCourseFeatureDAO
                        .findByCoachingCourseFeatureId(request.getCoachingCourseFeatureId()))
                .orElseThrow(() -> new InvalidRequestException(
                        "feature id not present : " + request.getCoachingCourseFeatureId()));
        ConverterUtil.setCoachingCourseFeatureData(request, coachingCourseFeatureEntity);
        try {
            return coachingCourseFeatureDAO.save(coachingCourseFeatureEntity);
        } catch (DataIntegrityViolationException ex) {
            throw new InvalidRequestException(ex.getMessage(), ex);
        }
    }

    public List<CoachingCourseFeatureEntity> findByInstituteIdAndName(Long id, String name) {
        return coachingCourseFeatureDAO.findByInstituteIdAndName(id, name);
    }


    public boolean isValidCourseFeatureIds(List<Long> ids) {
        List<Long> existingFeatureIds =
                coachingCourseFeatureDAO.findAllByCoachingCourseFeatureId(ids)
                        .stream().map(CoachingCourseFeatureEntity::getCoachingCourseFeatureId)
                        .collect(Collectors.toList());
        List<Long> invalidFeatureIds = ids.stream().filter(id -> !existingFeatureIds.contains(id))
                .collect(Collectors.toList());
        if (!invalidFeatureIds.isEmpty()) {
            throw new InvalidRequestException(
                    "Invalid course feature ids given : " + invalidFeatureIds);
        }
        return true;
    }

}
