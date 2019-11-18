package com.paytm.digital.education.database.repository;

import com.paytm.digital.education.database.entity.CoachingCtaEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

public interface CoachingCtaRepository extends MongoRepository<CoachingCtaEntity, ObjectId> {

    CoachingCtaEntity findByCtaId(Long ctaId);

    List<CoachingCtaEntity> findAllByCtaIdIn(Collection<Long> ctaIds);
}
