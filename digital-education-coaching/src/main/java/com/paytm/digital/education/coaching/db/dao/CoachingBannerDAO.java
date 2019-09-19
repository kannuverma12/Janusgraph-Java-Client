package com.paytm.digital.education.coaching.db.dao;

import com.paytm.digital.education.coaching.database.repository.SequenceGenerator;
import com.paytm.digital.education.database.entity.CoachingBannerEntity;
import com.paytm.digital.education.database.repository.CoachingBannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class CoachingBannerDAO {

    @Autowired
    private CoachingBannerRepository coachingBannerRepository;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    public CoachingBannerEntity save(CoachingBannerEntity coachingBannerEntity) {
        if (Objects.isNull(coachingBannerEntity.getCoachingBannerId())) {
            coachingBannerEntity.setCoachingBannerId(
                    sequenceGenerator
                            .getNextSequenceId(coachingBannerEntity.getClass().getSimpleName()));
        }
        return coachingBannerRepository.save(coachingBannerEntity);
    }

    public CoachingBannerEntity findByCoachingBannerId(Long coachingBannerEntity) {
        return coachingBannerRepository.findByCoachingBannerId(coachingBannerEntity);
    }

    public List<CoachingBannerEntity> findAll() {
        return this.coachingBannerRepository.findAll();
    }
}
