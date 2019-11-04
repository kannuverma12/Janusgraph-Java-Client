package com.paytm.digital.education.database.repository;

import com.paytm.digital.education.database.entity.CoachingInstituteEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoachingInstituteRepositoryNew
        extends MongoRepository<CoachingInstituteEntity, ObjectId> {
    CoachingInstituteEntity findByInstituteId(Long id);

    @Query(fields = "{'institute_id':1, _id : 0}")
    List<CoachingInstituteEntity> findAllByInstituteId(List<Long> ids);

    @Override List<CoachingInstituteEntity> findAll();

}
