package com.paytm.digital.education.database.repository;

import com.paytm.digital.education.database.entity.CoachingExamEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoachingExamRepositoryNew extends MongoRepository<CoachingExamEntity, ObjectId> {

    CoachingExamEntity findByCoachingExamId(Long coachingExamId);
}
