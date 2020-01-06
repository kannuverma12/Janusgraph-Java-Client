package com.paytm.digital.education.explore.controller;

import com.paytm.digital.education.database.entity.CTAConfigHolder;
import com.paytm.digital.education.database.entity.EducationEntityCTAConfig;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.Institute;
import com.paytm.digital.education.database.entity.School;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.enums.CTAEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

@RequiredArgsConstructor
@Service
public class CTAConfigDBService {
    private final CommonMongoRepository commonMongoRepository;
    private CommonMongoRepository underlyingCommonMongoRepository;

    @PostConstruct
    public void setUnderlyingCommonMongoRepository() {
        underlyingCommonMongoRepository = (CommonMongoRepository) AopProxyUtils
                .getSingletonTarget(commonMongoRepository);
    }

    public CTAConfigHolder getCTAConfigHolderOnlyCTAConfig(CTAEntity ctaEntity, long id) {
        return getCTAConfigHolder(commonMongoRepository, ctaEntity, id, asList("cta_config"));
    }

    public CTAConfigHolder getCTAConfigHolderAllFieldsBypassCache(CTAEntity ctaEntity, long id) {
        return getCTAConfigHolder(underlyingCommonMongoRepository, ctaEntity, id, null);
    }

    public CTAConfigHolder getCTAConfigHolderAllFields(CTAEntity entity) {
        return commonMongoRepository
                .getEntityByFields("cta_entity", entity.name(), EducationEntityCTAConfig.class, null);
    }

    public CTAConfigHolder getCTAConfigHolderAllFieldsByPassCache(CTAEntity entity) {
        return underlyingCommonMongoRepository
                .getEntityByFields("cta_entity", entity.name(), EducationEntityCTAConfig.class, null);
    }

    public void saveCTAConfigHolder(CTAConfigHolder ctaConfigHolder) {
        commonMongoRepository.saveOrUpdate(ctaConfigHolder);
    }

    public boolean resetCTATypesInEntity(CTAEntity ctaEntity) {
        commonMongoRepository.updateMulti(
                emptyMap(),
                emptyList(),
                Update.update("cta_config.cta_types", emptyList()),
                ctaEntity.getCorrespondingClass()
        );
        return true;
    }

    private CTAConfigHolder getCTAConfigHolder(
            CommonMongoRepository commonMongoRepository, CTAEntity ctaEntity, long id, List<String> fields) {
        String entityIdField = ctaEntity.name().toLowerCase() + "_id";
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
