package com.paytm.digital.education.database.repository;

import com.paytm.digital.education.database.entity.CoachingCenterEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoachingCenterRepository extends MongoRepository<CoachingCenterEntity, Long> {

    CoachingCenterEntity findByCenterId(Long centerId);
}
