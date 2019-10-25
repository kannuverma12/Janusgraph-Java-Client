package com.paytm.digital.education.database.repository;

import com.paytm.digital.education.database.entity.ItemEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends MongoRepository<ItemEntity, ObjectId> {

    List<ItemEntity> findByMerchantId(Long merchantId);
}
