package com.paytm.digital.education.database.dao;

import com.paytm.digital.education.database.entity.CoachingCourseEntity;
import com.paytm.digital.education.database.repository.CoachingProgramRepository;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.database.repository.SequenceGenerator;
import com.paytm.digital.education.metrics.annotations.NullValueAlert;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.mongodb.QueryOperators.AND;
import static com.mongodb.QueryOperators.IN;

@Component
public class CoachingCourseDAO {

    @Autowired
    CoachingProgramRepository programRepository;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    @Autowired
    private CommonMongoRepository commonMongoRepository;

    public CoachingCourseEntity save(@NonNull CoachingCourseEntity coachingProgramEntity) {
        if (Objects.isNull(coachingProgramEntity.getCourseId())) {
            coachingProgramEntity.setCourseId(sequenceGenerator
                    .getNextSequenceId(coachingProgramEntity.getClass().getSimpleName()));
        }
        return programRepository.save(coachingProgramEntity);
    }

    public List<CoachingCourseEntity> findAllByCourseId(@NonNull List<Long> ids) {
        return programRepository.findAllByCourseId(ids);
    }

    public CoachingCourseEntity findByProgramId(@NonNull Long id) {
        return programRepository.findByCourseId(id);
    }

    public List<CoachingCourseEntity> findAll() {
        return this.programRepository.findAll();
    }

    @NullValueAlert(mandatoryFields = "#mandatoryFields")
    public List<CoachingCourseEntity> findByCourseId(String courseIdField, long courseId,
            List<String> projectionFields, List<String> mandatoryFields) {
        Map<String, Object> searchRequest = new HashMap<>();
        searchRequest.put(courseIdField, courseId);
        return commonMongoRepository
                .findAll(searchRequest, CoachingCourseEntity.class,
                        projectionFields, AND);
    }

    @NullValueAlert(mandatoryFields = "#mandatoryFields")
    public List<CoachingCourseEntity> findByCourseIdsIn(String courseIdField,
            List<Long> courseIds,
            List<String> projectionFields, List<String> mandatoryFields) {
        return commonMongoRepository.getEntityFieldsByValuesIn(courseIdField, courseIds,
                CoachingCourseEntity.class, projectionFields);
    }

    @NullValueAlert(mandatoryFields = "#mandatoryFields")
    public List<CoachingCourseEntity> findByCoachingInstIdAndIsDynAndIsEnabledAndMerchantPIdsIn(
            String instituteIdField, long instituteId, String isDynamicField, Boolean isDynamic,
            String isEnabledField, Boolean isEnabled, String merchantProductIdField,
            List<String> merchantProductIds, String isOnboardedField, Boolean isOnboarded,
            List<String> projectionFields, List<String> mandatoryFields) {
        Map<String, Object> searchRequest = new HashMap<>();
        searchRequest.put(instituteIdField, instituteId);
        searchRequest.put(isDynamicField, isDynamic);
        searchRequest.put(isEnabledField, isEnabled);
        searchRequest.put(isOnboardedField, isOnboarded);
        Map<String, Object> merchantProductIdQueryMap = new HashMap<>();
        merchantProductIdQueryMap.put(IN, merchantProductIds);
        searchRequest.put(merchantProductIdField, merchantProductIdQueryMap);
        return commonMongoRepository.findAll(
                searchRequest, CoachingCourseEntity.class, projectionFields, AND);
    }

}
