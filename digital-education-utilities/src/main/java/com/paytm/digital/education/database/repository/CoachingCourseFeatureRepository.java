package com.paytm.digital.education.database.repository;

import com.paytm.digital.education.database.entity.CoachingCourseFeatureEntity;
import org.bson.types.ObjectId;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoachingCourseFeatureRepository
        extends MongoRepository<CoachingCourseFeatureEntity, ObjectId> {

    CoachingCourseFeatureEntity findByCoachingCourseFeatureId(Long id);

    @Query(value = "{coaching_course_feature_id: { $in: ?0 } })",
            fields = "{'coaching_course_feature_id':1, _id : 0}")
    List<CoachingCourseFeatureEntity> findAllByCoachingCourseFeatureId(List<Long> id);

    List<CoachingCourseFeatureEntity> findByInstituteIdAndName(Long id, String name);

    List<CoachingCourseFeatureEntity> findByInstituteId(Long id);

    @Cacheable(value = "findByCoachingCourseFeatureIdIn")
    List<CoachingCourseFeatureEntity> findByCoachingCourseFeatureIdIn(List<Long> featureIds);

    @Override List<CoachingCourseFeatureEntity> findAll();
}
