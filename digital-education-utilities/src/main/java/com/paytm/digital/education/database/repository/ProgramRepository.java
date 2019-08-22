package com.paytm.digital.education.database.repository;

import com.paytm.digital.education.database.entity.CoachingProgramEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProgramRepository extends MongoRepository<CoachingProgramEntity, ObjectId> {

    Optional<CoachingProgramEntity> findById(Long id);

    Optional<CoachingProgramEntity> findByProgramId(Long id);
}
