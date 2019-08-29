package com.paytm.digital.education.database.repository;

import com.paytm.digital.education.database.entity.TopRankerEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TopRankerRepository extends MongoRepository<TopRankerEntity, ObjectId> {

    TopRankerEntity findByTopRankerId(Long id);

}
