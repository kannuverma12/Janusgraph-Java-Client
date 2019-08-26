package com.paytm.digital.education.explore.service.impl;

import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.explore.constants.ExploreConstants;
import com.paytm.digital.education.explore.database.entity.Exam;
import com.paytm.digital.education.explore.database.entity.ExamPaytmKeys;
import com.paytm.digital.education.explore.database.entity.InstiPaytmKeys;
import com.paytm.digital.education.explore.database.entity.Institute;
import com.paytm.digital.education.explore.database.entity.PaytmKeys;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.request.dto.EntityData;
import com.paytm.digital.education.explore.response.dto.dataimport.CatalogDataIngestionError;
import com.paytm.digital.education.explore.service.ImportFromCatalogService;
import com.paytm.digital.education.mapping.ErrorEnum;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Map;
import java.util.HashMap;

@Service
@AllArgsConstructor
public class ImportFromCatalogServiceImpl implements ImportFromCatalogService {

    private CommonMongoRepository commonMongoRepository;

    @Override
    public List<CatalogDataIngestionError> ingestDataEntityWise(List<EntityData> entityDataList) {
        List<CatalogDataIngestionError> errors = new ArrayList<>();
        if (CollectionUtils.isEmpty(entityDataList)) {
            throw new BadRequestException(ErrorEnum.DATA_NOT_PRESENT,
                    ErrorEnum.DATA_NOT_PRESENT.getExternalMessage());
        }
        for (EntityData entityData : entityDataList) {
            String errorMessage = null;
            switch (entityData.getEducationEntity()) {
                case INSTITUTE:
                    errorMessage = ingestInInstituteCollection(entityData);
                    if (StringUtils.isNotBlank(errorMessage)) {
                        errors.add(CatalogDataIngestionError.builder()
                                .entityId(entityData.getEntityId())
                                .entity(entityData.getEducationEntity()).errorMessage(errorMessage)
                                .build());
                    }
                    break;
                case EXAM:
                    errorMessage = ingestInExamCollection(entityData);
                    if (StringUtils.isNotBlank(errorMessage)) {
                        errors.add(CatalogDataIngestionError.builder()
                                .entityId(entityData.getEntityId())
                                .entity(entityData.getEducationEntity()).errorMessage(errorMessage)
                                .build());
                    }
                    break;
                default:
                    errorMessage =
                            ErrorEnum.FUNCTIONALITY_NOT_SUPPORTED_FOR_ENTITY.getExternalMessage();
                    errors.add(CatalogDataIngestionError.builder().errorMessage(errorMessage)
                            .entityId(entityData.getEntityId())
                            .entity(entityData.getEducationEntity()).build());
                    break;
            }
        }
        return errors;
    }

    private String ingestInInstituteCollection(EntityData entityData) {
        if (Objects.isNull(entityData.getPid())) {
            return ErrorEnum.PID_MISSING.getExternalMessage();
        }
        InstiPaytmKeys instiPaytmKeys =
                InstiPaytmKeys.builder().mid(entityData.getMid()).pid(entityData.getPid())
                        .build();
        Map<String, Object> fields = new HashMap<>();
        fields.put(PaytmKeys.Constants.PAYTM_KEYS, instiPaytmKeys);
        String errorMessage = null;
        try {
            boolean updated = commonMongoRepository
                    .updateFields(fields, Institute.class, entityData.getEntityId(),
                            ExploreConstants.INSTITUTE_ID);
            errorMessage =
                    updated ? null : ErrorEnum.SOME_DATA_INCONSISTENCY.getExternalMessage();
        } catch (Exception e) {
            errorMessage = e.getMessage();
        }
        return errorMessage;
    }

    private String ingestInExamCollection(EntityData entityData) {
        if (Objects.isNull(entityData.getCollegePredictorId())) {
            return ErrorEnum.PREDICTOR_ID_MISSING.getExternalMessage();
        }
        Map<String, Object> fields = new HashMap<>();
        ExamPaytmKeys examPaytmKeys =
                ExamPaytmKeys.builder().collegePredictorId(entityData.getCollegePredictorId())
                        .build();
        fields.put(PaytmKeys.Constants.PAYTM_KEYS, examPaytmKeys);
        String errorMessage = null;
        try {
            boolean updated = commonMongoRepository
                    .updateFields(fields, Exam.class, entityData.getEntityId(),
                            ExploreConstants.EXAM_ID);
            errorMessage =
                    updated ? null : ErrorEnum.SOME_DATA_INCONSISTENCY.getExternalMessage();
        } catch (Exception e) {
            errorMessage = e.getMessage();
        }
        return errorMessage;
    }

}
