package com.paytm.digital.education.explore.controller;

import com.paytm.digital.education.database.entity.CTAConfig;
import com.paytm.digital.education.database.entity.CTAConfigHolder;
import com.paytm.digital.education.database.entity.EducationEntityCTAConfig;
import com.paytm.digital.education.enums.BulkOperationType;
import com.paytm.digital.education.enums.CTAEntity;
import com.paytm.digital.education.exception.NotFoundException;
import com.paytm.digital.education.explore.response.dto.detail.CTAConfigFetchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.paytm.digital.education.mapping.ErrorEnum.CTA_CONFIG_NOT_FOUND_FOR_ENTITY;
import static com.paytm.digital.education.mapping.ErrorEnum.NO_ENTITY_FOUND;

@RequiredArgsConstructor
@Service
public class CTAConfigService implements CTAConfigFetchService {

    private final CTAConfigDBService ctaConfigDBService;

    public CTAConfig getEducationEntityConfig(CTAEntity ctaEntity) {
        CTAConfigHolder ctaConfigHolder = ctaConfigDBService.getCTAConfigHolderAllFields(ctaEntity);
        if (ctaConfigHolder == null) {
            throw new NotFoundException(
                    CTA_CONFIG_NOT_FOUND_FOR_ENTITY,
                    CTA_CONFIG_NOT_FOUND_FOR_ENTITY.toString(),
                    new Object[]{ctaEntity.name()});
        }
        return ctaConfigHolder.getCTAConfig();
    }

    public CTAConfig putEducationEntityConfig(CTAEntity ctaEntity, CTAConfig ctaConfig) {
        CTAConfigHolder ctaConfigHolder = ctaConfigDBService.getCTAConfigHolderAllFieldsByPassCache(ctaEntity);
        if (ctaConfigHolder == null) {
            ctaConfigHolder = new EducationEntityCTAConfig(ctaEntity, ctaConfig);
        } else {
            ctaConfigHolder.setCTAConfig(ctaConfig);
        }
        ctaConfigDBService.saveCTAConfigHolder(ctaConfigHolder);
        return ctaConfigHolder.getCTAConfig();
    }

    public CTAConfig getEntityDocumentConfig(CTAEntity ctaEntity, long id) {
        CTAConfigHolder ctaConfigHolder = ctaConfigDBService.getCTAConfigHolderOnlyCTAConfig(ctaEntity, id);
        if (ctaConfigHolder == null) {
            throw new NotFoundException(
                    NO_ENTITY_FOUND, NO_ENTITY_FOUND.toString(), new Object[]{ctaEntity.name(), "id", id});
        }
        return ctaConfigHolder.getCTAConfig();
    }

    public CTAConfig putEntityDocumentConfig(CTAEntity ctaEntity, long id, CTAConfig ctaConfig) {
        CTAConfigHolder ctaConfigHolder = ctaConfigDBService.getCTAConfigHolderAllFieldsBypassCache(ctaEntity, id);
        if (ctaConfigHolder == null) {
            throw new NotFoundException(
                    NO_ENTITY_FOUND, NO_ENTITY_FOUND.toString(), new Object[]{ctaEntity.name(), "id", id});
        }
        ctaConfigHolder.setCTAConfig(ctaConfig);
        ctaConfigDBService.saveCTAConfigHolder(ctaConfigHolder);
        return ctaConfigHolder.getCTAConfig();
    }

    private boolean bulkResetEntity(CTAEntity ctaEntity) {
        return ctaConfigDBService.resetCTATypesInEntity(ctaEntity);
    }

    public boolean bulkOperation(CTAEntity ctaEntity, BulkOperationType bulkOperationType) {
        switch (bulkOperationType) {
            case RESET:
                return bulkResetEntity(ctaEntity);
            default:
                return false;
        }
    }

    @Override
    public CTAConfig fetchCTAConfig(CTAEntity ctaEntity, long id) {
        CTAConfigHolder ctaConfigHolder = ctaConfigDBService.getCTAConfigHolderOnlyCTAConfig(ctaEntity, id);
        return ctaConfigHolder.getCTAConfig();
    }

    @Override
    public CTAConfig fetchCTAConfig(CTAEntity ctaEntity) {
        CTAConfigHolder ctaConfigHolder = ctaConfigDBService.getCTAConfigHolderAllFields(ctaEntity);
        return ctaConfigHolder.getCTAConfig();
    }
}
