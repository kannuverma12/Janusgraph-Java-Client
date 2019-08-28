package com.paytm.digital.education.coaching.db.dao;

import com.paytm.digital.education.coaching.database.repository.SequenceGenerator;
import com.paytm.digital.education.database.entity.CoachingInstituteEntity;
import com.paytm.digital.education.database.repository.CoachingInstituteRepositoryNew;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CoachingInstituteDAO {

    @Autowired
    private CoachingInstituteRepositoryNew coachingInstituteRepositoryNew;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    public CoachingInstituteEntity save(CoachingInstituteEntity coachingInstituteEntity) {
        coachingInstituteEntity.setInstituteId(
                sequenceGenerator.getNextSequenceId(coachingInstituteEntity.getClass().getSimpleName()));
        return coachingInstituteRepositoryNew.save(coachingInstituteEntity);
    }

    public Optional<CoachingInstituteEntity> findByInstituteId(Long id) {
        return coachingInstituteRepositoryNew.findByInstituteId(id);
    }
}
