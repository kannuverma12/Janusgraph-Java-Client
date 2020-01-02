package com.paytm.digital.education.explore.service.helper.cta.producer;

import com.paytm.digital.education.constant.ExploreConstants;
import com.paytm.digital.education.enums.Client;
import com.paytm.digital.education.explore.enums.CTAType;
import com.paytm.digital.education.explore.response.dto.common.CTA;
import com.paytm.digital.education.explore.response.dto.detail.CTAInfoHolder;
import com.paytm.digital.education.utility.CommonUtil;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.paytm.digital.education.constant.ExploreConstants.DISPLAY_NAME;
import static com.paytm.digital.education.constant.ExploreConstants.ICON;
import static com.paytm.digital.education.explore.enums.CTAType.BROCHURE;

@RequiredArgsConstructor
@Service
public class BrochureCTAProducer extends AbstractCTAProducer {
    private static Logger log = LoggerFactory.getLogger(BrochureCTAProducer.class);

    @Override
    public CTAType getCTAType() {
        return BROCHURE;
    }

    @Override
    public CTA cta(CTAInfoHolder ctaInfoHolder, Map<String, String> ctaConfigMap, Client client) {
        return getBrochureCTA(ctaInfoHolder, ctaConfigMap, client);
    }

    private CTA getBrochureCTA(CTAInfoHolder ctaInfoHolder, Map<String, String> ctaConfiguration, Client client) {
        String brochureUrl = ctaInfoHolder.getBrochureUrl();
        String key = ctaInfoHolder.ctaDbPropertyKey();
        String namespace = ctaInfoHolder.getCorrespondingEntity().name().toLowerCase();
        String name = ctaConfiguration.get(DISPLAY_NAME);
        String relativeIcon =
                ctaConfiguration.getOrDefault(ICON, ExploreConstants.CTA_LOGO_PLACEHOLDER);
        if (!checkIfNameExists(name, CTAType.COMPARE, key, namespace)) {
            return null;
        }
        return CTA.builder()
                .type(CTAType.BROCHURE)
                .label(name)
                .logo(CommonUtil.getAbsoluteUrl(relativeIcon, ExploreConstants.CTA))
                .url(brochureUrl)
                .build();
    }
}

