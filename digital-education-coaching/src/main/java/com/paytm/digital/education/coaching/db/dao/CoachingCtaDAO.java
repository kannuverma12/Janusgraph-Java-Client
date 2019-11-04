package com.paytm.digital.education.coaching.db.dao;

import com.paytm.digital.education.database.entity.CoachingCtaEntity;
import com.paytm.digital.education.database.repository.CoachingCtaRepository;
import com.paytm.digital.education.database.repository.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class CoachingCtaDAO {

    private CoachingCtaRepository ctaRepository;

    private SequenceGenerator sequenceGenerator;

    public CoachingCtaEntity save(@NonNull CoachingCtaEntity coachingCtaEntity) {

        if (Objects.isNull(coachingCtaEntity.getCtaId())) {
            coachingCtaEntity.setCtaId(sequenceGenerator
                    .getNextSequenceId(coachingCtaEntity.getClass().getSimpleName()));
        }

        return ctaRepository.save(coachingCtaEntity);
    }

    public CoachingCtaEntity findByCtaId(@NonNull Long id) {
        return ctaRepository.findByCtaId(id);
    }

    public List<CoachingCtaEntity> findAll() {
        return ctaRepository.findAll();
    }
}
