package com.paytm.digital.education.serviceimpl.helper;

import com.paytm.digital.education.annotation.EduCache;
import com.paytm.digital.education.database.entity.EntitySourceMappingEntity;
import com.paytm.digital.education.database.repository.EntitySourceMappingRepository;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.paytm.digital.education.enums.EntitySourceType.PAYTM;

@Component
@AllArgsConstructor
public class EntitySourceMappingHelper {
    private static final Logger                        log =
            LoggerFactory.getLogger(EntitySourceMappingProvider.class);
    private              EntitySourceMappingRepository entitySourceMappingRepository;

    @EduCache(cache = "paytm_source_entity_ids", shouldCacheNull = false)
    public List<Long> getPaytmSourceEntitiesForEntity(EducationEntity educationEntity) {
        return Optional.ofNullable(
                entitySourceMappingRepository
                        .findByEducationEntityAndSource(educationEntity.name(), PAYTM.name()))
                .orElse(new ArrayList<>()).stream().filter(e -> Objects.nonNull(e.getEntityId()))
                .map(EntitySourceMappingEntity::getEntityId)
                .collect(Collectors.toList());
    }
}
