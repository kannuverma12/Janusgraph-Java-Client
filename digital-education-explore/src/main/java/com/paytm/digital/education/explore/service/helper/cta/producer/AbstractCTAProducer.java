package com.paytm.digital.education.explore.service.helper.cta.producer;

import com.paytm.digital.education.constant.ExploreConstants;
import com.paytm.digital.education.enums.Client;
import com.paytm.digital.education.explore.enums.CTAType;
import com.paytm.digital.education.explore.response.dto.common.CTA;
import com.paytm.digital.education.explore.response.dto.detail.CTAInfoHolder;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public abstract class AbstractCTAProducer implements CTAProducer {
    private static Logger log = LoggerFactory.getLogger(AbstractCTAProducer.class);

    public abstract CTAType getCTAType();

    public abstract CTA cta(CTAInfoHolder ctaInfoHolder, Map<String, String> ctaConfigMap, Client client);

    @Override
    public CTA produceCTA(CTAInfoHolder ctaInfoHolder, Map<String, Object> parentCTAConfigMap, Client client) {
        if (parentCTAConfigMap == null) {
            return null;
        }
        if (checkIfCTAConfigExists(ctaInfoHolder, parentCTAConfigMap)) {
            return null;
        }
        Map<String, String> ctaConfigMap = (Map<String, String>) parentCTAConfigMap
                .get(CTAType.FEE.name().toLowerCase());
        return cta(ctaInfoHolder, ctaConfigMap, client);
    }

    protected boolean checkIfNameExists(String name, String activeName, CTAType ctaType, String key,
                                        String namespace) {
        if (StringUtils.isBlank(activeName) || StringUtils.isBlank(name)) {
            log.warn("CTA name not found for {} key {} namespace {}", ctaType, key, namespace);
            return false;
        }
        return true;
    }

    protected boolean checkIfNameExists(String name, CTAType ctaType, String key,
                                        String namespace) {
        if (StringUtils.isBlank(name)) {
            log.warn("CTA name not found for {} key {} namespace {}", ctaType, key, namespace);
            return false;
        }
        return true;
    }

    private boolean checkIfCTAConfigExists(CTAInfoHolder ctaInfoHolder, Map<String, Object> parentCTAConfigMap) {
        CTAType ctaType = getCTAType();
        String key = ctaInfoHolder.ctaDbPropertyKey();
        String namespace = ctaInfoHolder.getCorrespondingEntity().name().toLowerCase();
        if (parentCTAConfigMap.containsKey(ctaType.name().toLowerCase())) {
            return true;
        }
        log.error("Cta config not found for {} component : {}, key : {} namespace :{}, ", ctaType,
                ExploreConstants.EXPLORE_COMPONENT, key, namespace);
        return false;
    }
}
