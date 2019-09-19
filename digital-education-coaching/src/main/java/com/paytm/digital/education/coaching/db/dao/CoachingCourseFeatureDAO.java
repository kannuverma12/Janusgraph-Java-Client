package com.paytm.digital.education.coaching.db.dao;

import com.paytm.digital.education.coaching.database.repository.SequenceGenerator;
import com.paytm.digital.education.database.entity.CoachingCenterEntity;
import com.paytm.digital.education.database.entity.CoachingCourseFeatureEntity;
import com.paytm.digital.education.database.repository.CoachingCourseFeatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class CoachingCourseFeatureDAO {

    @Autowired
    private CoachingCourseFeatureRepository coachingCourseFeatureRepository;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    public CoachingCourseFeatureEntity save(
            CoachingCourseFeatureEntity coachingCourseFeatureEntity) {
        if (Objects.isNull(coachingCourseFeatureEntity.getCoachingCourseFeatureId())) {
            coachingCourseFeatureEntity.setCoachingCourseFeatureId(
                    sequenceGenerator.getNextSequenceId(
                            coachingCourseFeatureEntity.getClass().getSimpleName()));
        }
        return coachingCourseFeatureRepository.save(coachingCourseFeatureEntity);
    }

    public CoachingCourseFeatureEntity findByCoachingCourseFeatureId(Long id) {
        return coachingCourseFeatureRepository.findByCoachingCourseFeatureId(id);
    }

    public List<CoachingCourseFeatureEntity> findAllByCoachingCourseFeatureId(List<Long> id) {
        return coachingCourseFeatureRepository.findAllByCoachingCourseFeatureId(id);
    }

    public List<CoachingCourseFeatureEntity> findByInstituteIdAndName(Long id, String name) {
        return coachingCourseFeatureRepository.findByInstituteIdAndName(id, name);
    }

    public List<CoachingCourseFeatureEntity> findAll() {
        return this.coachingCourseFeatureRepository.findAll();
    }

}
