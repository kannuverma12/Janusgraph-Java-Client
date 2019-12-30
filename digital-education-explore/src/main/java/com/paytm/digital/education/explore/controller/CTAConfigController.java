package com.paytm.digital.education.explore.controller;

import com.paytm.digital.education.database.entity.CTAConfig;
import com.paytm.digital.education.enums.CTAEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static com.paytm.digital.education.constant.ExploreConstants.EDUCATION_BASE_URL;

@RestController
@Validated
@RequestMapping(EDUCATION_BASE_URL)
@RequiredArgsConstructor
public class CTAConfigController {

    private final CTAConfigService ctaConfigService;

    @RequestMapping(method = RequestMethod.GET, path = "/auth/v1/{entity}/cta_config")
    public CTAConfig getEntityCTAConfig(@PathVariable("entity") CTAEntity entity) {
        return ctaConfigService.getEducationEntityConfig(entity);

    }

    @RequestMapping(method = RequestMethod.GET, path = "/auth/v1/{entity}/{entity_id}/cta_config")
    public CTAConfig getEntityDocCTAConfig(@PathVariable("entity") CTAEntity entity,
                                           @PathVariable("entity_id") long entityId) {
        return ctaConfigService.getEntityDocumentConfig(entity, entityId);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/auth/v1/{entity}/cta_config")
    public CTAConfig putEntityCTAConfig(@PathVariable("entity") CTAEntity entity,
                                        @RequestBody CTAConfig ctaConfig) {
        return ctaConfigService.putEducationEntityConfig(entity, ctaConfig);

    }

    @RequestMapping(method = RequestMethod.PUT, path = "/auth/v1/{entity}/{entity_id}/cta_config")
    public CTAConfig putEntityDocCTAConfig(@PathVariable("entity") CTAEntity entity,
                                           @PathVariable("entity_id") long entityId,
                                           @RequestBody CTAConfig ctaConfig) {
        return ctaConfigService.putEntityDocumentConfig(entity, entityId, ctaConfig);
    }
}
