package com.paytm.digital.education.database.repository;

import com.paytm.digital.education.database.entity.PaytmSourceData;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaytmSourceDataRepository extends MongoRepository<PaytmSourceData, ObjectId> {

    PaytmSourceData findByEntityIdAndEducationEntityAndSource(Long entityId, String educationEntity, String source);
}
