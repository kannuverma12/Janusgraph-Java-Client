package com.paytm.digital.education.database.repository;

import com.paytm.digital.education.database.entity.MerchantStreamEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MerchantStreamRepository extends MongoRepository<MerchantStreamEntity, ObjectId> {

    MerchantStreamEntity findByMerchantIdAndMerchantStream(String merchantId, String merchantStream);
}
