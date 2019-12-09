package com.paytm.digital.education.database.repository;

import com.paytm.digital.education.database.entity.EntitySourceMappingEntity;
import com.paytm.digital.education.enums.EntitySourceType;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntitySourceMappingRepository
        extends MongoRepository<EntitySourceMappingEntity, ObjectId> {

    EntitySourceMappingEntity findByEntityIdAndEducationEntity(Long entityId,
            String educationEntity);

    EntitySourceType findByEducationEntityAndEntityId(String name, Long entityId);
}
