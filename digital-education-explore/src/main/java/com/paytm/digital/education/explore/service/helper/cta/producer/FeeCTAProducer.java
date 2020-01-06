package com.paytm.digital.education.explore.service.helper.cta.producer;

import com.paytm.digital.education.constant.ExploreConstants;
import com.paytm.digital.education.enums.Client;
import com.paytm.digital.education.explore.enums.CTAType;
import com.paytm.digital.education.explore.response.dto.common.CTA;
import com.paytm.digital.education.explore.response.dto.detail.CTAInfoHolder;
import com.paytm.digital.education.explore.service.external.FeeUrlGenerator;
import com.paytm.digital.education.utility.CommonUtil;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.paytm.digital.education.explore.enums.CTAType.FEE;

@RequiredArgsConstructor
@Service
@Getter
public class FeeCTAProducer extends AbstractCTAProducer {
    private static Logger log = LoggerFactory.getLogger(FeeCTAProducer.class);

    private final FeeUrlGenerator feeUrlGenerator;

    @Accessors(fluent = true)
    private final CTAType ctaType = FEE;

    @Override
    public CTA cta(CTAInfoHolder ctaInfoHolder, Map<String, String> ctaConfigMap, Client client) {
        return getFeeCTA(ctaInfoHolder, ctaConfigMap, client);
    }

    private CTA getFeeCTA(CTAInfoHolder ctaInfoHolder, Map<String, String> ctaConfiguration, Client client) {
        String key = ctaInfoHolder.ctaDbPropertyKey();
        String namespace = ctaInfoHolder.getCorrespondingEntity().name().toLowerCase();
        Long pid = ctaInfoHolder.getPid();

        if (pid == null) {
            return null;
        }

        String name = ctaConfiguration.get(CTA.Constants.DISPLAY_NAME);
        String icon = ctaConfiguration
                .getOrDefault(CTA.Constants.ICON, ExploreConstants.CTA_LOGO_PLACEHOLDER);
        String feeUrl = feeUrlGenerator.generateUrl(pid, client);

        if (!checkIfNameExists(name, key, namespace)) {
            return null;
        }

        if (StringUtils.isBlank(feeUrl)) {
            log.error("Unable to build fee url for pid {}", pid);
            return null;
        }

        CTA cta = CTA.builder()
                .label(name)
                .logo(CommonUtil.getAbsoluteUrl(icon, ExploreConstants.CTA))
                .type(CTAType.FEE)
                .url(feeUrl)
                .build();
        return cta;
    }
}
