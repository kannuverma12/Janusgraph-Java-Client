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
import static com.paytm.digital.education.explore.enums.CTAType.SHARE;

@Service
@Getter
public class ShareCTAProducer extends AbstractCTAProducer {
    private static Logger log = LoggerFactory.getLogger(ShareCTAProducer.class);

    @Accessors(fluent = true)
    private final CTAType ctaType = SHARE;

    @Override
    public CTA cta(CTAInfoHolder ctaInfoHolder, Map<String, String> ctaConfigMap, Client client) {
        if (APP.equals(client)) {
            return getShareCTA(ctaInfoHolder, ctaConfigMap);
        }
        return null;
    }

    private CTA getShareCTA(CTAInfoHolder ctaInfoHolder, Map<String, String> ctaConfiguration) {
        String key = ctaInfoHolder.ctaDbPropertyKey();
        String namespace = ctaInfoHolder.getCorrespondingEntity().name().toLowerCase();
        String name = ctaConfiguration.get(DISPLAY_NAME);
        String relativeIconUrl =
                ctaConfiguration.getOrDefault(ICON, ExploreConstants.CTA_LOGO_PLACEHOLDER);
        if (!checkIfNameExists(name, key, namespace)) {
            return null;
        }
        return CTA.builder()
                .type(CTAType.SHARE)
                .label(name)
                .logo(CommonUtil.getAbsoluteUrl(relativeIconUrl, ExploreConstants.CTA))
                .build();
    }
}


