package com.paytm.digital.education.admin.service.impl;

import com.paytm.digital.education.admin.request.EntitySourceMappingData;
import com.paytm.digital.education.admin.request.EntitySourceMappingRequest;
import com.paytm.digital.education.admin.response.EntitySourceMappingResponse;
import com.paytm.digital.education.admin.validator.EntitySourceMappingDataValidator;
import com.paytm.digital.education.database.entity.EntitySourceMappingEntity;
import com.paytm.digital.education.database.repository.EntitySourceMappingRepository;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.enums.EntitySourceType;
import com.paytm.digital.education.utility.CommonUtils;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.paytm.digital.education.constant.ExploreConstants.FAILED;
import static com.paytm.digital.education.constant.ExploreConstants.PARTIAL_FAILED;
import static com.paytm.digital.education.constant.ExploreConstants.SUCCESS;

@Service
@AllArgsConstructor
public class EntitySourceMappingServiceImpl {
    private static final Logger log =
            LoggerFactory.getLogger(EntitySourceMappingServiceImpl.class);

    private EntitySourceMappingRepository    entitySourceMappingRepository;
    private EntitySourceMappingDataValidator validator;

    public EntitySourceMappingResponse saveEntitySourceMapping(
            EntitySourceMappingRequest entitySourceMappingRequest) {

        if (CollectionUtils.isEmpty(entitySourceMappingRequest.getEntitySourceMappingData())) {
            log.error("No entity source mapping data found in create request, skipping");
            return null;
        }

        validator.validateEntitySourceMappingData(entitySourceMappingRequest);

        List<EntitySourceMappingEntity> entitySourceMappingEntityList = new ArrayList<>();
        EntitySourceMappingResponse entitySourceMappingResponse =
                new EntitySourceMappingResponse();
        entitySourceMappingResponse
                .setEducationEntity(entitySourceMappingRequest.getEducationEntity());
        entitySourceMappingResponse.setStatus(FAILED);
        try {
            for (EntitySourceMappingData entitySourceMappingData : entitySourceMappingRequest
                    .getEntitySourceMappingData()) {

                EntitySourceMappingEntity entitySourceMappingEntity = entitySourceMappingRepository
                        .findByEntityIdAndEducationEntity(entitySourceMappingData.getEntityId(),
                                entitySourceMappingRequest.getEducationEntity().name());

                if (Objects.isNull(entitySourceMappingEntity)) {
                    entitySourceMappingEntity = new EntitySourceMappingEntity();
                    entitySourceMappingEntity
                            .setEducationEntity(entitySourceMappingRequest.getEducationEntity());
                }

                CommonUtils
                        .copyNonNullProperties(entitySourceMappingData, entitySourceMappingEntity);
                entitySourceMappingEntity.setActive(true);
                EntitySourceMappingEntity entitySourceMappingEntityUpdated =
                        entitySourceMappingRepository.save(entitySourceMappingEntity);
                if (Objects.nonNull(entitySourceMappingEntityUpdated)) {
                    entitySourceMappingEntityList.add(entitySourceMappingEntityUpdated);
                }
            }

            entitySourceMappingResponse.setData(entitySourceMappingEntityList);
            if (!CollectionUtils.isEmpty(entitySourceMappingEntityList)
                    && entitySourceMappingEntityList.size() == entitySourceMappingRequest
                    .getEntitySourceMappingData()
                    .size()) {
                entitySourceMappingResponse.setStatus(SUCCESS);
            } else if (!CollectionUtils.isEmpty(entitySourceMappingEntityList)
                    && entitySourceMappingEntityList.size() > 0) {
                entitySourceMappingResponse.setStatus(PARTIAL_FAILED);
            }
        } catch (Exception e) {
            log.error(
                    "Exception occurred while saving entity source mapping, returning failure response.",
                    e);
        }
        return entitySourceMappingResponse;
    }

    public EntitySourceMappingResponse getEntitySourceMapping(EducationEntity entity,
            Long entityId) {
        EntitySourceMappingResponse entitySourceMappingResponse = new EntitySourceMappingResponse();
        entitySourceMappingResponse.setStatus(FAILED);
        entitySourceMappingResponse.setEducationEntity(entity);

        EntitySourceMappingEntity entitySourceMappingEntity = entitySourceMappingRepository
                .findByEntityIdAndEducationEntity(entityId,
                        entity.name());
        if (Objects.nonNull(entitySourceMappingEntity)) {
            entitySourceMappingResponse.setStatus(SUCCESS);
            entitySourceMappingResponse
                    .setData(Collections.singletonList(entitySourceMappingEntity));
        }
        return entitySourceMappingResponse;
    }

    public EntitySourceMappingResponse deleteEntitySourceMapping(
            @Valid EntitySourceMappingRequest entitySourceMappingRequest) {
        if (CollectionUtils.isEmpty(entitySourceMappingRequest.getEntitySourceMappingData())) {
            log.error("No entity source mapping data found in create request, skipping");
            return null;
        }
        EntitySourceMappingResponse entitySourceMappingResponse = new EntitySourceMappingResponse();
        entitySourceMappingResponse
                .setEducationEntity(entitySourceMappingRequest.getEducationEntity());
        List<EntitySourceMappingEntity> entitySourceMappingResponseList = new ArrayList<>();

        try {
            for (EntitySourceMappingData entitySourceMappingData : entitySourceMappingRequest
                    .getEntitySourceMappingData()) {
                EntitySourceMappingEntity entitySourceMappingUpdated = entitySourceMappingRepository
                        .findByEntityIdAndEducationEntity(entitySourceMappingData.getEntityId(),
                                entitySourceMappingRequest.getEducationEntity().name());

                if (Objects.isNull(entitySourceMappingUpdated)) {
                    log.info(
                            "Entity Source Mapping is not present in db for entityId :{}, entity: {}, skipping",
                            entitySourceMappingData.getEntityId(),
                            entitySourceMappingRequest.getEducationEntity());
                    continue;
                }
                entitySourceMappingUpdated.setActive(false);
                EntitySourceMappingEntity entitySourceMappingEntityUpdated =
                        entitySourceMappingRepository.save(entitySourceMappingUpdated);
                if (Objects.nonNull(entitySourceMappingEntityUpdated)) {
                    entitySourceMappingResponseList.add(entitySourceMappingEntityUpdated);
                }
            }
        } catch (Exception e) {
            log.error(
                    "Exception occurred while saving entity source mapping, returning failure response.",
                    e);
        }

        entitySourceMappingResponse.setData(entitySourceMappingResponseList);
        entitySourceMappingResponse.setStatus(FAILED);

        if (!CollectionUtils.isEmpty(entitySourceMappingResponseList)
                && entitySourceMappingResponseList.size() == entitySourceMappingRequest
                .getEntitySourceMappingData()
                .size()) {
            entitySourceMappingResponse.setStatus(SUCCESS);
        } else if (!CollectionUtils.isEmpty(entitySourceMappingResponseList)
                && entitySourceMappingResponseList.size() > 0) {
            entitySourceMappingResponse.setStatus(PARTIAL_FAILED);
        }
        return entitySourceMappingResponse;
    }
}
