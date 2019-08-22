package com.paytm.digital.education.database.repository;

import com.paytm.digital.education.database.entity.CoachingInstitute;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoachingInstituteRepositoryNew
        extends MongoRepository<CoachingInstitute, ObjectId> {
    CoachingInstitute findByInstituteId(Long id);
}
