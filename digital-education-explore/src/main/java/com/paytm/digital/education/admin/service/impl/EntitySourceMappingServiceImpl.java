package com.paytm.digital.education.admin.service.impl;

import com.paytm.digital.education.admin.request.EntitySourceMappingData;
import com.paytm.digital.education.admin.request.EntitySourceMappingRequest;
import com.paytm.digital.education.admin.response.EntitySourceMappingResponse;
import com.paytm.digital.education.admin.validator.EntitySourceMappingDataValidator;
import com.paytm.digital.education.database.entity.EntitySourceMappingEntity;
import com.paytm.digital.education.database.repository.EntitySourceMappingRepository;
import com.paytm.digital.education.enums.EducationEntity;
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

    private EntitySourceMappingRepository entitySourceMappingRepository;
    private EntitySourceMappingDataValidator validator;

    public EntitySourceMappingResponse saveEntitySourceMapping(
            EntitySourceMappingRequest entitySourceMappingRequest) {

        if (CollectionUtils.isEmpty(entitySourceMappingRequest.getEntitySourceMappingData())) {
            log.error("No entity source mapping data found in create request, skipping");
            return null;
        }

        validator.validateEntitySourceMappingData(entitySourceMappingRequest);

        List<EntitySourceMappingEntity> entitySourceMappingEntityList = new ArrayList<>();
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

            CommonUtils.copyNonNullProperties(entitySourceMappingData, entitySourceMappingEntity);
            entitySourceMappingEntity.setActive(true);
            EntitySourceMappingEntity entitySourceMappingEntityUpdated =
                    entitySourceMappingRepository.save(entitySourceMappingEntity);
            entitySourceMappingEntityList.add(entitySourceMappingEntityUpdated);
        }
        EntitySourceMappingResponse entitySourceMappingResponse = new EntitySourceMappingResponse();
        entitySourceMappingResponse
                .setEducationEntity(entitySourceMappingRequest.getEducationEntity());
        entitySourceMappingResponse.setData(entitySourceMappingEntityList);
        entitySourceMappingResponse.setStatus(SUCCESS);
        return entitySourceMappingResponse;
    }

    public EntitySourceMappingResponse getEntitySourceMapping(EducationEntity entity,
            Long entityId) {
        EntitySourceMappingResponse entitySourceMappingResponse = new EntitySourceMappingResponse();
        EntitySourceMappingEntity entitySourceMappingEntity = entitySourceMappingRepository
                .findByEntityIdAndEducationEntity(entityId,
                        entity.name());
        entitySourceMappingResponse.setStatus(SUCCESS);
        entitySourceMappingResponse.setEducationEntity(entity);
        entitySourceMappingResponse.setData(Collections.singletonList(entitySourceMappingEntity));
        return entitySourceMappingResponse;
    }

    public EntitySourceMappingResponse deleteEntitySourceMapping(
            @Valid EntitySourceMappingRequest entitySourceMappingRequest) {
        if (CollectionUtils.isEmpty(entitySourceMappingRequest.getEntitySourceMappingData())) {
            log.error("No entity source mapping data found in create request, skipping");
            return null;
        }
        EntitySourceMappingResponse entitySourceMappingResponse = new EntitySourceMappingResponse();
        List<EntitySourceMappingEntity> paytmSourceDataUpdatedList = new ArrayList<>();

        for (EntitySourceMappingData entitySourceMappingData : entitySourceMappingRequest
                .getEntitySourceMappingData()) {
            EntitySourceMappingEntity paytmSourceDataInDb = entitySourceMappingRepository
                    .findByEntityIdAndEducationEntity(entitySourceMappingData.getEntityId(),
                            entitySourceMappingRequest.getEducationEntity().name());

            if (Objects.isNull(paytmSourceDataInDb)) {
                log.info(
                        "Entity Source Mapping is not present in db for entityId :{}, entity: {}, skipping",
                        entitySourceMappingData.getEntityId(),
                        entitySourceMappingRequest.getEducationEntity());
                continue;
            }
            paytmSourceDataInDb.setActive(false);
            EntitySourceMappingEntity entitySourceMappingEntityUpdated =
                    entitySourceMappingRepository.save(paytmSourceDataInDb);
            paytmSourceDataUpdatedList.add(entitySourceMappingEntityUpdated);
        }

        entitySourceMappingResponse
                .setEducationEntity(entitySourceMappingRequest.getEducationEntity());
        entitySourceMappingResponse.setData(paytmSourceDataUpdatedList);
        entitySourceMappingResponse.setStatus(FAILED);

        if (!CollectionUtils.isEmpty(paytmSourceDataUpdatedList)
                && paytmSourceDataUpdatedList.size() == entitySourceMappingRequest
                .getEntitySourceMappingData()
                .size()) {
            entitySourceMappingResponse.setStatus(SUCCESS);
        } else if (!CollectionUtils.isEmpty(paytmSourceDataUpdatedList)
                && paytmSourceDataUpdatedList.size() > 0) {
            entitySourceMappingResponse.setStatus(PARTIAL_FAILED);
        }
        return entitySourceMappingResponse;
    }
}
