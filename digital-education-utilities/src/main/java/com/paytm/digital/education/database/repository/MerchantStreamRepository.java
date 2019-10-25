package com.paytm.digital.education.database.repository;

import com.paytm.digital.education.database.entity.MerchantStreamEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface MerchantStreamRepository extends MongoRepository<MerchantStreamEntity, ObjectId> {

    @Query("{'active':true, 'merchant_id':?0}")
    List<MerchantStreamEntity> findAllByMerchantId(String merchantId);

    MerchantStreamEntity findByMerchantIdAndStream(String merchantId, String stream);

    List<MerchantStreamEntity> findAllByMerchantIdAndStreamIn(String merchantId, List<String> streams);
}
