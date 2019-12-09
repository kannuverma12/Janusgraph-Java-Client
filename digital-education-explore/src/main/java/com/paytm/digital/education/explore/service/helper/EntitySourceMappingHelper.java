package com.paytm.digital.education.explore.service.helper;

import com.paytm.digital.education.admin.service.impl.EntitySourceMappingServiceImpl;
import com.paytm.digital.education.admin.validator.EntitySourceMappingDataValidator;
import com.paytm.digital.education.database.repository.EntitySourceMappingRepository;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.enums.EntitySourceType;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class EntitySourceMappingHelper {

    private static final Logger log =
            LoggerFactory.getLogger(EntitySourceMappingServiceImpl.class);

    private EntitySourceMappingRepository entitySourceMappingRepository;

    @Cacheable(value = "entity_source_mapping", key = "'entity_source_'+ #entity + '.'+ #entityId")
    public EntitySourceType getEntitySourceMappingEntity(EducationEntity entity,
            Long entityId) {
        EntitySourceType entitySourceType;

        try {
            entitySourceType = entitySourceMappingRepository
                    .findByEducationEntityAndEntityId(entity.name(), entityId);
        } catch (Exception e) {
            log.error(
                    "Exception occurred while finding source type for entity :{}, entityId :{}, "
                            + "returning default source (merchant)", e, entity, entityId);
            entitySourceType = EntitySourceType.C360;
        }
        return entitySourceType;
    }
}
