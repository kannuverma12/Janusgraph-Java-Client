package com.paytm.digital.education.database.repository;

import com.paytm.digital.education.database.entity.CoachingCourseFeatureEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoachingCourseFeatureRepository
        extends MongoRepository<CoachingCourseFeatureEntity, ObjectId> {

    CoachingCourseFeatureEntity findByCoachingCourseFeatureId(Long id);

}
