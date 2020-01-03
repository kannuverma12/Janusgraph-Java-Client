package com.paytm.digital.education.explore.controller;

import com.paytm.digital.education.database.entity.CTAConfigHolder;
import com.paytm.digital.education.database.entity.EducationEntityCTAConfig;
import com.paytm.digital.education.database.entity.Exam;
import com.paytm.digital.education.database.entity.Institute;
import com.paytm.digital.education.database.entity.School;
import com.paytm.digital.education.database.repository.CommonMongoRepository;
import com.paytm.digital.education.enums.CTAEntity;
import com.paytm.digital.education.enums.EducationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.ImmutableMap.of;
import static java.util.Arrays.asList;

@RequiredArgsConstructor
@Service
public class CTAConfigDBService {
    private final CommonMongoRepository commonMongoRepository;
    private CommonMongoRepository underlyingCommonMongoRepository;

    private static final Map<EducationEntity, CTAEntity> map = of(
            EducationEntity.SCHOOL, CTAEntity.SCHOOL,
            EducationEntity.EXAM, CTAEntity.EXAM,
            EducationEntity.INSTITUTE, CTAEntity.INSTITUTE
    );

    @PostConstruct
    public void setUnderlyingCommonMongoRepository() {
        underlyingCommonMongoRepository = (CommonMongoRepository) AopProxyUtils
                .getSingletonTarget(commonMongoRepository);
    }

    public CTAConfigHolder getCTAConfigHolderOnlyCTAConfig(CTAEntity ctaEntity, long id) {
        return getCTAConfigHolder(commonMongoRepository, ctaEntity, id, asList("cta_config"));
    }

    public CTAConfigHolder getCTAConfigHolderAllFields(CTAEntity ctaEntity, long id) {
        return getCTAConfigHolder(commonMongoRepository, ctaEntity, id, null);
    }

    public CTAConfigHolder getCTAConfigHolderOnlyCTAConfigBypassCache(CTAEntity ctaEntity, long id) {
        return getCTAConfigHolder(underlyingCommonMongoRepository, ctaEntity, id, asList("cta_config"));
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
