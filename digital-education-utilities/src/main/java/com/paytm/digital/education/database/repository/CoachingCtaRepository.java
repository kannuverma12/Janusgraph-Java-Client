package com.paytm.digital.education.database.repository;

import com.paytm.digital.education.database.entity.CoachingCtaEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CoachingCtaRepository extends MongoRepository<CoachingCtaEntity, ObjectId> {

    CoachingCtaEntity findByCtaId(Long ctaId);
}
