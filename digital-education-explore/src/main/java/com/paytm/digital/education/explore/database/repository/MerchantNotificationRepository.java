package com.paytm.digital.education.explore.database.repository;

import com.paytm.digital.education.database.entity.MerchantNotification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantNotificationRepository extends MongoRepository<MerchantNotification, String> {

}
