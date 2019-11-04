package com.paytm.digital.education.database.repository;

import com.paytm.digital.education.database.entity.CoachingCourseEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoachingCourseRepository
        extends MongoRepository<CoachingCourseEntity, ObjectId> {

    Optional<CoachingCourseEntity> findByCourseId(Long id);

    @Override List<CoachingCourseEntity> findAll();

}
