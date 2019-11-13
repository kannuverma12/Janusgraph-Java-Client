package com.paytm.digital.education.database.dao;

import com.paytm.digital.education.database.entity.CoachingCenterEntity;
import com.paytm.digital.education.database.repository.CoachingCenterRepository;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.database.repository.SequenceGenerator;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class CoachingCenterDAO {

    @Autowired
    private CoachingCenterRepository coachingCenterRepository;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    @Autowired
    private CommonMongoRepository commonMongoRepository;

    public CoachingCenterEntity save(@NonNull CoachingCenterEntity coachingCenterEntity) {
        if (Objects.isNull(coachingCenterEntity.getCenterId())) {
            coachingCenterEntity.setCenterId(sequenceGenerator
                    .getNextSequenceId(coachingCenterEntity.getClass().getSimpleName()));
        }
        return coachingCenterRepository.save(coachingCenterEntity);
    }

    public CoachingCenterEntity findByCenterId(@NonNull Long id) {
        return coachingCenterRepository.findByCenterId(id);
    }

    public List<CoachingCenterEntity> findAll() {
        return this.coachingCenterRepository.findAll();
    }

    public List<CoachingCenterEntity> findByInstituteId(String instituteIdField, long instituteId,
            List<String> projectionFields) {
        return commonMongoRepository.getEntitiesByIdAndFields(
                instituteIdField, instituteId, CoachingCenterEntity.class, projectionFields);
    }

    public List<CoachingCenterEntity> findByCenterIdsIn(String centerIdField,
            List<Long> centerIds,
            List<String> projectionFields) {
        return this.commonMongoRepository.getEntityFieldsByValuesIn(centerIdField, centerIds,
                CoachingCenterEntity.class, projectionFields);
    }
}
