package com.paytm.digital.education.explore.database.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.paytm.digital.education.database.entity.CronProperties;

public interface CronPropertiesRepository extends MongoRepository<CronProperties, String> {

    CronProperties findByCronName(String cronName);
    
}
