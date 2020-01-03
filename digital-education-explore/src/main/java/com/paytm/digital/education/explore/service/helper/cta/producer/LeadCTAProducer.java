package com.paytm.digital.education.explore.service.helper.cta.producer;

import com.paytm.digital.education.constant.ExploreConstants;
import com.paytm.digital.education.enums.Client;
import com.paytm.digital.education.explore.enums.CTAType;
import com.paytm.digital.education.explore.response.dto.common.CTA;
import com.paytm.digital.education.explore.response.dto.detail.CTAInfoHolder;
import com.paytm.digital.education.utility.CommonUtil;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.paytm.digital.education.constant.ExploreConstants.DISPLAY_NAME;
import static com.paytm.digital.education.constant.ExploreConstants.ICON;
import static com.paytm.digital.education.explore.enums.CTAType.LEAD;
import static com.paytm.digital.education.explore.response.dto.common.CTA.Constants.ACTIVE_DISPLAY_NAME;
import static com.paytm.digital.education.explore.response.dto.common.CTA.Constants.ACTIVE_ICON;
import static com.paytm.digital.education.explore.response.dto.common.CTA.Constants.CLIENT;

@Service
public class LeadCTAProducer extends AbstractCTAProducer {
    private static Logger log = LoggerFactory.getLogger(LeadCTAProducer.class);

    @Override
    public CTAType getCTAType() {
        return LEAD;
    }

    @Override
    public CTA cta(CTAInfoHolder ctaInfoHolder, Map<String, String> ctaConfigMap, Client client) {
        return getLeadCTA(ctaInfoHolder, ctaConfigMap);
    }

    private CTA getLeadCTA(CTAInfoHolder ctaInfoHolder, Map<String, String> ctaConfigurationMap) {
        boolean isThirdPartyClient = ctaInfoHolder.isClient();
        String key = ctaInfoHolder.ctaDbPropertyKey();
        String namespace = ctaInfoHolder.getCorrespondingEntity().name().toLowerCase();
        String leadLabel = null;
        String activeLabel = null;
        if (isThirdPartyClient) {
            leadLabel = ctaConfigurationMap.get(DISPLAY_NAME + CLIENT);
            activeLabel = ctaConfigurationMap.get(ACTIVE_DISPLAY_NAME + CLIENT);
        } else {
            leadLabel = ctaConfigurationMap.get(DISPLAY_NAME);
            activeLabel = ctaConfigurationMap.get(ACTIVE_DISPLAY_NAME);
        }
        String relativeUrl = ctaConfigurationMap
                .getOrDefault(ICON, ExploreConstants.CTA_LOGO_PLACEHOLDER);
        String activeRelativeUrl = ctaConfigurationMap
                .getOrDefault(ACTIVE_ICON, ExploreConstants.CTA_LOGO_PLACEHOLDER);
        if (!checkIfNameExists(leadLabel, activeLabel, CTAType.LEAD, key, namespace)) {
            return null;
        }
        return CTA.builder()
                .type(CTAType.LEAD)
                .label(leadLabel)
                .activeText(activeLabel)
                .activeLogo(CommonUtil.getAbsoluteUrl(activeRelativeUrl, ExploreConstants.CTA))
                .logo(CommonUtil.getAbsoluteUrl(relativeUrl, ExploreConstants.CTA))
                .build();
    }
}

