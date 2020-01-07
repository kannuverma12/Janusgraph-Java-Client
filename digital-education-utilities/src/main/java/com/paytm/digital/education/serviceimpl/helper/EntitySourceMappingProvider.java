package com.paytm.digital.education.serviceimpl.helper;

import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.enums.EntitySourceType;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.paytm.digital.education.enums.EntitySourceType.C360;
import static com.paytm.digital.education.enums.EntitySourceType.PAYTM;

@Component
@AllArgsConstructor
public class EntitySourceMappingProvider {

    private static final Logger                    log =
            LoggerFactory.getLogger(EntitySourceMappingProvider.class);
    private              EntitySourceMappingHelper entitySourceMappingHelper;

    public EntitySourceType getSourceAndEntitiesMapping(EducationEntity entity,
            Long entityId) {
        EntitySourceType entitySourceType = null;

        try {
            entitySourceType = Optional.ofNullable(
                    entitySourceMappingHelper.getPaytmSourceEntitiesForEntity(entity))
                    .orElse(new ArrayList<>()).contains(entityId) ? PAYTM : C360;
        } catch (Exception e) {
            log.error(
                    "Exception occurred while finding source type for entity :{}, entityId :{}, "
                            + "returning default source (merchant)", e, entity, entityId);
        }
        return Objects.nonNull(entitySourceType) ? entitySourceType : C360;
    }



    public Map<EntitySourceType, List<Long>> getSourceAndEntitiesMapping(EducationEntity entity,
            List<Long> entityIds) {
        Map<EntitySourceType, List<Long>> sourceAndEntityIdsMap = new HashMap<>();

        try {
            List<Long> entitiesWithSourcePaytm =
                    Optional.ofNullable(
                            entitySourceMappingHelper.getPaytmSourceEntitiesForEntity(entity))
                            .orElse(new ArrayList<>());

            if (!CollectionUtils.isEmpty(entityIds)) {
                entitiesWithSourcePaytm.retainAll(entityIds);
                if (!CollectionUtils.isEmpty(entitiesWithSourcePaytm)) {
                    sourceAndEntityIdsMap.put(PAYTM, entitiesWithSourcePaytm);
                    entityIds.removeAll(entitiesWithSourcePaytm);
                }
                sourceAndEntityIdsMap.put(C360, entityIds);
            }
        } catch (Exception e) {
            log.error(
                    "Exception occurred while finding source type for entity :{}, entityId :{}, "
                            + "returning default source (merchant)", e, entity, entityIds);
            sourceAndEntityIdsMap = new HashMap<>();
            sourceAndEntityIdsMap.put(C360, entityIds);
        }
        return sourceAndEntityIdsMap;
    }

}
