package com.paytm.digital.education.database.repository;

import com.paytm.digital.education.database.entity.CoachingBannerEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoachingBannerRepository
        extends MongoRepository<CoachingBannerEntity, ObjectId> {

    CoachingBannerEntity findByCoachingBannerId(Long id);

}
