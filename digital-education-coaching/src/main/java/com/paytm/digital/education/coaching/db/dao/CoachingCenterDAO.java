package com.paytm.digital.education.coaching.db.dao;

import com.paytm.digital.education.coaching.database.repository.SequenceGenerator;
import com.paytm.digital.education.database.entity.CoachingCenterEntity;
import com.paytm.digital.education.database.repository.CoachingCenterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoachingCenterDAO {

    @Autowired
    private CoachingCenterRepository coachingCenterRepository;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    public CoachingCenterEntity save(CoachingCenterEntity coachingCenterEntity) {
        coachingCenterEntity.setCenterId(sequenceGenerator
                .getNextSequenceId(coachingCenterEntity.getClass().getSimpleName()));
        return coachingCenterRepository.save(coachingCenterEntity);
    }

    public CoachingCenterEntity findByCenterId(Long id) {
        return coachingCenterRepository.findByCenterId(id);
    }
}
