package com.paytm.digital.education.database.repository;

import com.paytm.digital.education.database.entity.CoachingCourseEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoachingProgramRepository
        extends MongoRepository<CoachingCourseEntity, ObjectId> {

    CoachingCourseEntity findById(Long id);

    CoachingCourseEntity findByCourseIdAndName(long programId, String name);

    CoachingCourseEntity findByCourseId(Long id);

    @Query(value = "{course_id: { $in: ?0 } })", fields = "{'course_id':1, _id : 0}")
    List<CoachingCourseEntity> findAllByCourseId(List<Long> ids);

    @Override List<CoachingCourseEntity> findAll();
}
