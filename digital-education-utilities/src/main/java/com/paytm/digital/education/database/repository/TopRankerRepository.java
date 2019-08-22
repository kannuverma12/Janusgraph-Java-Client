package com.paytm.digital.education.database.repository;

import com.paytm.digital.education.database.entity.TopRankerEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TopRankerRepository extends MongoRepository<TopRankerEntity, ObjectId> {

    Optional<TopRankerEntity> findByTopRankerId(Long id);
}
