package com.paytm.digital.education.database.repository;

import com.paytm.digital.education.database.entity.CoachingCenterEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoachingCenterRepository extends MongoRepository<CoachingCenterEntity, ObjectId> {

    CoachingCenterEntity findByCenterId(Long centerId);

    @Override List<CoachingCenterEntity> findAll();
}
