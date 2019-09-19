package com.paytm.digital.education.coaching.db.dao;

import com.paytm.digital.education.coaching.database.repository.SequenceGenerator;
import com.paytm.digital.education.database.entity.CoachingCenterEntity;
import com.paytm.digital.education.database.repository.CoachingCenterRepository;
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
}
