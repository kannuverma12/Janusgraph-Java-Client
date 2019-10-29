package com.paytm.digital.education.database.repository;

import com.paytm.digital.education.database.entity.CoachingExamEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoachingExamRepositoryNew extends MongoRepository<CoachingExamEntity, ObjectId> {

    CoachingExamEntity findByCoachingExamId(Long coachingExamId);

    @Override List<CoachingExamEntity> findAll();

}
