package com.paytm.digital.education.explore.service.impl;

import com.google.common.collect.ImmutableMap;
import com.paytm.digital.education.constant.ExploreConstants;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.ExamPaytmKeys;
import com.paytm.digital.education.database.entity.InstiPaytmKeys;
import com.paytm.digital.education.database.entity.Institute;
import com.paytm.digital.education.database.entity.PaytmKeys;
import com.paytm.digital.education.database.entity.School;
import com.paytm.digital.education.database.entity.SchoolPaytmKeys;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.exception.EducationException;
import com.paytm.digital.education.explore.request.dto.EntityData;
import com.paytm.digital.education.explore.response.dto.dataimport.StaticDataIngestionResponse;
import com.paytm.digital.education.explore.service.IngestStaticDataService;
import com.paytm.digital.education.explore.validators.ExploreValidator;
import com.paytm.digital.education.mapping.ErrorEnum;
import com.paytm.digital.education.utility.CommonUtil;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service
@AllArgsConstructor
public class IngestStaticDataServiceImpl implements IngestStaticDataService {

    private CommonMongoRepository commonMongoRepository;
    private ExploreValidator      exploreValidator;

    @Override
    public List<StaticDataIngestionResponse> ingestDataEntityWise(List<EntityData> entityDataList) {
        List<StaticDataIngestionResponse> errors = new ArrayList<>();
        if (CollectionUtils.isEmpty(entityDataList)) {
            throw new BadRequestException(ErrorEnum.DATA_NOT_PRESENT,
                    ErrorEnum.DATA_NOT_PRESENT.getExternalMessage());
        }
        for (EntityData entityData : entityDataList) {
            if (Objects.isNull(entityData.getEducationEntity())) {
                errors.add(buildCatalogDataIngestionError(entityData.getEntityId(), ErrorEnum.ENTITY_MISSING));
                continue;
            }
            String errorMessage = null;
            switch (entityData.getEducationEntity()) {
                case INSTITUTE:
                case SCHOOL:
                    errorMessage = ingestInInstituteOrSchoolCollection(entityData);
                    if (StringUtils.isNotBlank(errorMessage)) {
                        errors.add(StaticDataIngestionResponse.builder()
                                .entityId(entityData.getEntityId())
                                .entity(entityData.getEducationEntity()).errorMessage(errorMessage)
                                .build());
                    }
                    break;
                case EXAM:
                    errorMessage = ingestInExamCollection(entityData);
                    if (StringUtils.isNotBlank(errorMessage)) {
                        errors.add(StaticDataIngestionResponse.builder()
                                .entityId(entityData.getEntityId())
                                .entity(entityData.getEducationEntity()).errorMessage(errorMessage)
                                .build());
                    }
                    break;
                default:
                    errorMessage =
                            ErrorEnum.FUNCTIONALITY_NOT_SUPPORTED_FOR_ENTITY.getExternalMessage();
                    errors.add(StaticDataIngestionResponse.builder().errorMessage(errorMessage)
                            .entityId(entityData.getEntityId())
                            .entity(entityData.getEducationEntity()).build());
                    break;
            }
        }
        return errors;
    }

    private StaticDataIngestionResponse buildCatalogDataIngestionError(
            Long entityId, ErrorEnum errorEnum) {
        return StaticDataIngestionResponse
                .builder()
                .errorMessage(errorEnum.getExternalMessage())
                .entityId(entityId)
                .build();
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

    private PaytmKeys writeEntityDataToCorrespondingPaytmKeys(EntityData entityData) {
        EducationEntity educationEntity = entityData.getEducationEntity();
        switch (educationEntity) {
            case INSTITUTE:
                return JsonUtils.convertValue(entityData, InstiPaytmKeys.class);
            case SCHOOL:
                return JsonUtils.convertValue(entityData, SchoolPaytmKeys.class);
            default:
                return null;
        }
    }

    private String ingestInInstituteOrSchoolCollection(EntityData entityData) {
        if (Objects.isNull(entityData.getPid())) {
            return String.format(ErrorEnum.PID_MISSING.getExternalMessage(),
                    entityData.getEducationEntity().name());
        }
        String errorMessage = null;
        PaytmKeys paytmKeys = writeEntityDataToCorrespondingPaytmKeys(entityData);
        exploreValidator.validateAndThrowException(paytmKeys);
        try {
            long updated = commonMongoRepository
                    .updateFields(
                            ImmutableMap.of(
                                    PaytmKeys.Constants.PAYTM_KEYS,
                                    paytmKeys),
                            getCorrespondingClassForEnum(entityData.getEducationEntity()),
                            entityData.getEntityId(),
                            CommonUtil.getIdFieldNameFromEducationEntity(entityData.getEducationEntity()));
            if (updated == 0) {
                throw new EducationException(
                        ErrorEnum.INVALID_ENTITY_ID, new Object[]{entityData.getEducationEntity().name()});
            }
        } catch (Exception e) {
            errorMessage = e.getMessage();
        }
        return errorMessage;
    }

    private String ingestInExamCollection(EntityData examEntityData) {

        Map<String, Object> fields = new HashMap<>();
        ExamPaytmKeys examPaytmKeys =
                ExamPaytmKeys.builder().formId(examEntityData.getFormId())
                        .collegePredictorId(examEntityData.getCollegePredictorId())
                        .webFormUriPrefix(examEntityData.getWebFormUriPrefix())
                        .termsAndConditions(examEntityData.getTermsAndConditions())
                        .privacyPolicies(examEntityData.getPrivacyPolicies())
                        .disclaimer(examEntityData.getDisclaimer())
                        .build();
        fields.put(PaytmKeys.Constants.PAYTM_KEYS, examPaytmKeys);
        String errorMessage = null;
        try {
            long updated = commonMongoRepository
                    .updateFields(fields, Exam.class, examEntityData.getEntityId(),
                            ExploreConstants.EXAM_ID);
            errorMessage =
                    updated != 0 ? null : ErrorEnum.INVALID_EXAM_ID.getExternalMessage();
        } catch (Exception e) {
            errorMessage = e.getMessage();
        }
        return errorMessage;
    }

}
