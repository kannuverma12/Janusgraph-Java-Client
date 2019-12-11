package com.paytm.digital.education.serviceimpl.helper;

import com.paytm.digital.education.database.entity.EntitySourceMappingEntity;
import com.paytm.digital.education.database.repository.EntitySourceMappingRepository;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.enums.EntitySourceType;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class EntitySourceMappingProvider {

    private static final Logger                        log =
            LoggerFactory.getLogger(EntitySourceMappingProvider.class);
    private              EntitySourceMappingRepository entitySourceMappingRepository;

    //@Cacheable(value = "entity_source_mapping", key = "'entity_source_'+ #entity + '.'+ #entityId")
    public EntitySourceType getSourceAndEntitiesMapping(EducationEntity entity,
            Long entityId) {
        EntitySourceType entitySourceType = null;

        try {
            entitySourceType = Optional.ofNullable(entitySourceMappingRepository
                    .findByEntityIdAndEducationEntity(entityId, entity.name()))
                    .map(EntitySourceMappingEntity::getSource).orElse(EntitySourceType.C360);
        } catch (Exception e) {
            log.error(
                    "Exception occurred while finding source type for entity :{}, entityId :{}, "
                            + "returning default source (merchant)", e, entity, entityId);
        }
        return Objects.nonNull(entitySourceType) ? entitySourceType : EntitySourceType.C360;
    }

    public Map<EntitySourceType, List<Long>> getSourceAndEntitiesMapping(EducationEntity entity,
            List<Long> entityId) {
        Map<EntitySourceType, List<Long>> sourceAndEntityIdsMap = new HashMap<>();

        try {
            List<EntitySourceMappingEntity> entitySourceMappings = entitySourceMappingRepository
                    .findByEducationEntityAndEntityIdIn(entity.name(), entityId);
            sourceAndEntityIdsMap =
                    Optional.ofNullable(entitySourceMappings).orElse(new ArrayList<>()).stream()
                            .filter(e -> Objects.nonNull(e.getSource()))
                            .collect(Collectors.groupingBy(e -> e.getSource(),
                                    Collectors.mapping(e -> e.getEntityId(), Collectors.toList())));
        } catch (Exception e) {
            log.error(
                    "Exception occurred while finding source type for entity :{}, entityId :{}, "
                            + "returning default source (merchant)", e, entity, entityId);
        }
        return sourceAndEntityIdsMap;
    }

}
