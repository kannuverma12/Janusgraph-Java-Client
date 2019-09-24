package com.paytm.digital.education.coaching.db.dao;

import com.paytm.digital.education.coaching.database.repository.SequenceGenerator;
import com.paytm.digital.education.database.entity.CoachingExamEntity;
import com.paytm.digital.education.database.repository.CoachingExamRepositoryNew;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class CoachingExamDAO {

    @Autowired
    CoachingExamRepositoryNew coachingCenterRepository;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    public CoachingExamEntity save(@NonNull CoachingExamEntity coachingExamEntity) {
        if (Objects.isNull(coachingExamEntity.getCoachingExamId())) {
            coachingExamEntity.setCoachingExamId(sequenceGenerator
                    .getNextSequenceId(coachingExamEntity.getClass().getSimpleName()));
        }
        return coachingCenterRepository.save(coachingExamEntity);
    }

    public CoachingExamEntity findByExamId(@NonNull Long id) {
        return coachingCenterRepository.findByCoachingExamId(id);
    }

    public List<CoachingExamEntity> findAll() {
        return this.coachingCenterRepository.findAll();
    }

}
