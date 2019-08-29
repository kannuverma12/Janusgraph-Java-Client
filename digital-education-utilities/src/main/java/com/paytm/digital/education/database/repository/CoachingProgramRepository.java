package com.paytm.digital.education.database.repository;

import com.paytm.digital.education.database.entity.CoachingCourseEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoachingProgramRepository
        extends MongoRepository<CoachingCourseEntity, ObjectId> {

    CoachingCourseEntity findById(Long id);

    CoachingCourseEntity findByCourseIdAndName(long programId, String name);

    CoachingCourseEntity findByCourseId(Long id);
}
