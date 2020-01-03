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
import static com.paytm.digital.education.enums.Client.APP;
import static com.paytm.digital.education.explore.enums.CTAType.BROCHURE;
import static com.paytm.digital.education.explore.response.dto.common.CTA.Constants.ACTIVE_DISPLAY_NAME;
import static com.paytm.digital.education.explore.response.dto.common.CTA.Constants.ACTIVE_ICON;
import static com.paytm.digital.education.explore.response.dto.common.CTA.Constants.WEB;

@Service
public class ShortListCTAProducer extends AbstractCTAProducer {
    private static Logger log = LoggerFactory.getLogger(BrochureCTAProducer.class);

    @Override
    public CTAType getCTAType() {
        return BROCHURE;
    }

    @Override
    public CTA cta(CTAInfoHolder ctaInfoHolder, Map<String, String> ctaConfigMap, Client client) {
        return getShortListCTA(ctaInfoHolder, ctaConfigMap, client);
    }

    private CTA getShortListCTA(CTAInfoHolder ctaInfoHolder, Map<String, String> ctaConfiguration, Client client) {
        String key = ctaInfoHolder.ctaDbPropertyKey();
        String namespace = ctaInfoHolder.getCorrespondingEntity().name().toLowerCase();
        String shortListLabel = null;
        String activeLabel = null;
        if (APP.equals(client)) {
            shortListLabel = ctaConfiguration.get(DISPLAY_NAME);
            activeLabel = ctaConfiguration.get(ACTIVE_DISPLAY_NAME);
        } else {
            shortListLabel = ctaConfiguration.get(DISPLAY_NAME + WEB);
            activeLabel = ctaConfiguration.get(ACTIVE_DISPLAY_NAME + WEB);
        }

        if (!checkIfNameExists(shortListLabel, activeLabel, CTAType.SHORTLIST, key, namespace)) {
            return null;
        }

        String relativeIcon =
                ctaConfiguration.getOrDefault(ICON, ExploreConstants.CTA_LOGO_PLACEHOLDER);
        String relativeAbsActiveIcon =
                ctaConfiguration.getOrDefault(ACTIVE_ICON, ExploreConstants.CTA_LOGO_PLACEHOLDER);
        return CTA.builder()
                .type(CTAType.SHORTLIST)
                .label(shortListLabel)
                .activeLogo(CommonUtil.getAbsoluteUrl(relativeAbsActiveIcon, ExploreConstants.CTA))
                .activeText(activeLabel)
                .logo(CommonUtil.getAbsoluteUrl(relativeIcon, ExploreConstants.CTA))
                .build();
    }
}

