package com.paytm.digital.education.database.repository;

import com.paytm.digital.education.database.entity.CoachingInstituteEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoachingInstituteRepositoryNew
        extends MongoRepository<CoachingInstituteEntity, ObjectId> {

    Optional<CoachingInstituteEntity> findByInstituteId(Long id);
}
