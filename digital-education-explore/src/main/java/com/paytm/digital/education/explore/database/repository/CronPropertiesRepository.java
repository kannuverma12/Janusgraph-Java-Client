package com.paytm.digital.education.explore.database.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.paytm.digital.education.explore.database.entity.CronProperties;

public interface CronPropertiesRepository extends MongoRepository<CronProperties, String> {

    CronProperties findByKey(String key);
    
}
