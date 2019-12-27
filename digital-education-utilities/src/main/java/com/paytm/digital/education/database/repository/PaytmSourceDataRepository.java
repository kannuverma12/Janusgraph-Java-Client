package com.paytm.digital.education.database.repository;

import com.paytm.digital.education.database.entity.PaytmSourceDataEntity;
import com.paytm.digital.education.database.entity.School;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaytmSourceDataRepository
        extends MongoRepository<PaytmSourceDataEntity, ObjectId> {

    PaytmSourceDataEntity findByEntityIdAndEducationEntity(Long entityId,
            String educationEntity);

}
