package com.paytm.digital.education.explore.controller;

import com.paytm.digital.education.database.entity.CTAConfig;
import com.paytm.digital.education.database.entity.CTAConfigHolder;
import com.paytm.digital.education.database.entity.EducationEntityCTAConfig;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.Institute;
import com.paytm.digital.education.database.entity.School;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.enums.CTAEntity;
import com.paytm.digital.education.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

import static com.paytm.digital.education.mapping.ErrorEnum.CTA_CONFIG_NOT_FOUND_FOR_ENTITY;
import static com.paytm.digital.education.mapping.ErrorEnum.NO_ENTITY_FOUND;
import static java.util.Arrays.asList;

@RequiredArgsConstructor
@Service
public class CTAConfigService {

    private final CommonMongoRepository commonMongoRepository;

    public CTAConfig getEducationEntityConfig(CTAEntity ctaEntity) {
        EducationEntityCTAConfig educationEntityConfig = commonMongoRepository
                .getEntityByFields("cta_entity", ctaEntity.name(), EducationEntityCTAConfig.class, null);
        if (educationEntityConfig == null) throw new NotFoundException(
                CTA_CONFIG_NOT_FOUND_FOR_ENTITY,
                CTA_CONFIG_NOT_FOUND_FOR_ENTITY.toString(),
                new Object[]{ctaEntity.name()});
        return educationEntityConfig.getCTAConfig();
    }

    public CTAConfig getEntityDocumentConfig(CTAEntity ctaEntity, long id) {
        CTAConfigHolder ctaConfigHolder = ctaConfigHolderFromEntity(ctaEntity, id);
        if (ctaConfigHolder == null) throw new NotFoundException(
                NO_ENTITY_FOUND, NO_ENTITY_FOUND.toString(), new Object[]{ctaEntity.name(), "id", id});
        return ctaConfigHolder.getCTAConfig();
    }

    public CTAConfig putEntityDocumentConfig(CTAEntity ctaEntity, long id, CTAConfig ctaConfig) {
        String entityIdField = ctaEntity.name().toLowerCase() + "_id";
        switch (ctaEntity) {
            case SCHOOL:
                School school = commonMongoRepository.getEntityByFields(entityIdField, id, School.class, null);
                school.setCTAConfig(ctaConfig);
                commonMongoRepository.saveOrUpdate(school);
                return ctaConfig;
            case INSTITUTE:
                Institute institute = commonMongoRepository.getEntityByFields(
                        entityIdField, id, Institute.class, null);
                institute.setCTAConfig(ctaConfig);
                commonMongoRepository.saveOrUpdate(institute);
                return ctaConfig;
            case EXAM:
                Exam exam = commonMongoRepository.getEntityByFields(entityIdField, id, Exam.class, null);
                exam.setCTAConfig(ctaConfig);
                commonMongoRepository.saveOrUpdate(exam);
                return ctaConfig;
            default:
                return null;
        }
    }

    public CTAConfig putEducationEntityConfig(CTAEntity ctaEntity, CTAConfig ctaConfig) {
        EducationEntityCTAConfig educationEntityCTAConfig = commonMongoRepository
                .getEntityByFields("cta_entity", ctaEntity.name(), EducationEntityCTAConfig.class, null);
        if (educationEntityCTAConfig == null) {
            educationEntityCTAConfig = new EducationEntityCTAConfig(ctaEntity, ctaConfig);
        } else {
            educationEntityCTAConfig.setCTAConfig(ctaConfig);
        }
        commonMongoRepository.saveOrUpdate(educationEntityCTAConfig);
        return ctaConfig;
    }

    private CTAConfigHolder ctaConfigHolderFromEntity(CTAEntity ctaEntity, long id) {
        String entityIdField = ctaEntity.name().toLowerCase() + "_id";
        List<String> fields = asList("cta_config");
        switch (ctaEntity) {
            case INSTITUTE:
                return commonMongoRepository.getEntityByFields(entityIdField, id, Institute.class, fields);
            case SCHOOL:
                return commonMongoRepository.getEntityByFields(entityIdField, id, School.class, fields);
            case EXAM:
                return commonMongoRepository.getEntityByFields(entityIdField, id, Exam.class, fields);
            default:
                return null;
        }
    }
}
