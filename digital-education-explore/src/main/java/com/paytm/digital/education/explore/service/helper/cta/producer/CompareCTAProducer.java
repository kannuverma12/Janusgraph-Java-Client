package com.paytm.digital.education.explore.service.helper.cta.producer;


import com.paytm.digital.education.constant.ExploreConstants;
import com.paytm.digital.education.enums.Client;
import com.paytm.digital.education.explore.enums.CTAType;
import com.paytm.digital.education.explore.response.dto.common.CTA;
import com.paytm.digital.education.explore.response.dto.detail.CTAInfoHolder;
import com.paytm.digital.education.utility.CommonUtil;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.paytm.digital.education.constant.ExploreConstants.DISPLAY_NAME;
import static com.paytm.digital.education.constant.ExploreConstants.ICON;
import static com.paytm.digital.education.enums.Client.APP;
import static com.paytm.digital.education.explore.enums.CTAType.COMPARE;
import static com.paytm.digital.education.explore.response.dto.common.CTA.Constants.ACTIVE_DISPLAY_NAME;

@Service
@Getter
public class CompareCTAProducer extends AbstractCTAProducer {
    private static Logger log = LoggerFactory.getLogger(CompareCTAProducer.class);

    @Accessors(fluent = true)
    private final CTAType ctaType = COMPARE;

    @Override
    public CTA cta(CTAInfoHolder ctaInfoHolder, Map<String, String> ctaConfigMap, Client client) {
        if (APP.equals(client)) {
            return null;
        }
        return getCompareCTA(ctaInfoHolder, ctaConfigMap);
    }

    private CTA getCompareCTA(CTAInfoHolder ctaInfoHolder, Map<String, String> ctaConfiguration) {
        String key = ctaInfoHolder.ctaDbPropertyKey();
        String namespace = ctaInfoHolder.getCorrespondingEntity().name().toLowerCase();
        String name = ctaConfiguration.get(DISPLAY_NAME);
        String activeName = ctaConfiguration.get(ACTIVE_DISPLAY_NAME);
        String relativeIconUrl = ctaConfiguration.get(ICON);
        if (!checkIfNameExists(name, activeName, CTAType.COMPARE, key, namespace)) {
            return null;
        }
        return CTA.builder()
                .type(CTAType.COMPARE)
                .label(name)
                .logo(CommonUtil.getAbsoluteUrl(relativeIconUrl, ExploreConstants.CTA))
                .activeText(activeName)
                .build();
    }
}


