package com.paytm.digital.education.database.repository;

import com.paytm.digital.education.database.entity.CoachingCourseFeatureEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoachingCourseFeatureRepository
        extends MongoRepository<CoachingCourseFeatureEntity, ObjectId> {

    CoachingCourseFeatureEntity findByCoachingCourseFeatureId(Long id);

    List<CoachingCourseFeatureEntity> findAllByCoachingCourseFeatureId(List<Long> id);

    List<CoachingCourseFeatureEntity> findByInstituteIdAndName(Long id,String name);

    List<CoachingCourseFeatureEntity> findByInstituteId(Long id);

    List<CoachingCourseFeatureEntity> findByCoachingCourseFeatureIdIn(List<Long> featureIds);

    @Override List<CoachingCourseFeatureEntity> findAll();
}
