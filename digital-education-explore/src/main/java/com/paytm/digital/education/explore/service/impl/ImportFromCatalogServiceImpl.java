package com.paytm.digital.education.explore.service.impl;

import com.google.common.collect.ImmutableMap;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.explore.constants.ExploreConstants;
import com.paytm.digital.education.explore.database.entity.Exam;
import com.paytm.digital.education.explore.database.entity.ExamPaytmKeys;
import com.paytm.digital.education.explore.database.entity.Institute;
import com.paytm.digital.education.explore.database.entity.PaytmKeys;
import com.paytm.digital.education.explore.database.entity.School;
import com.paytm.digital.education.explore.database.repository.CommonMongoRepository;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.request.dto.EntityData;
import com.paytm.digital.education.explore.response.dto.dataimport.CatalogDataIngestionError;
import com.paytm.digital.education.explore.service.ImportFromCatalogService;
import com.paytm.digital.education.explore.utility.CommonUtil;
import com.paytm.digital.education.mapping.ErrorEnum;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
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
                case SCHOOL:
                    errorMessage = ingestInInstituteOrSchoolCollection(entityData);
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

    private Class<? extends Object> getCorrespondingClassForEnum(EducationEntity educationEntity) {
        switch (educationEntity) {
            case SCHOOL:
                return School.class;
            case EXAM:
                return Exam.class;
            case INSTITUTE:
                return Institute.class;
            default:
                return null;
        }
    }

    private String ingestInInstituteOrSchoolCollection(EntityData entityData) {
        if (Objects.isNull(entityData.getPid())) {
            return ErrorEnum.PID_MISSING.getExternalMessage();
        }
        final List<String> IGNORABLE_KEYS = Arrays.asList("entity_id", "entity");
        Map<String, Object> keys = JsonUtils.convertValue(entityData, HashMap.class);
        IGNORABLE_KEYS.forEach(keys::remove);
        String errorMessage = null;
        try {
            long updated = commonMongoRepository
                    .updateFields(
                            ImmutableMap.of(
                                    PaytmKeys.Constants.PAYTM_KEYS,
                                    keys),
                            getCorrespondingClassForEnum(entityData.getEducationEntity()),
                            entityData.getEntityId(),
                            CommonUtil.getIdFieldNameFromEducationEntity(entityData.getEducationEntity()));
            errorMessage =
                    updated != 0 ? null : ErrorEnum.INVALID_INSTITUTE_ID.getExternalMessage();
        } catch (Exception e) {
            errorMessage = e.getMessage();
        }
        return errorMessage;
    }

    private String ingestInExamCollection(EntityData entityData) {
        Map<String, Object> fields = new HashMap<>();
        ExamPaytmKeys examPaytmKeys =
                ExamPaytmKeys.builder().formId(entityData.getFormId())
                        .collegePredictorId(entityData.getCollegePredictorId())
                        .build();
        fields.put(PaytmKeys.Constants.PAYTM_KEYS, examPaytmKeys);
        String errorMessage = null;
        try {
            long updated = commonMongoRepository
                    .updateFields(fields, Exam.class, entityData.getEntityId(),
                            ExploreConstants.EXAM_ID);
            errorMessage =
                    updated != 0 ? null : ErrorEnum.INVALID_EXAM_ID.getExternalMessage();
        } catch (Exception e) {
            errorMessage = e.getMessage();
        }
        return errorMessage;
    }

}
