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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.paytm.digital.education.enums.Client.APP;
import static com.paytm.digital.education.explore.enums.CTAType.PREDICTOR;

@Service
@Getter
public class PredictorCTAProducer extends AbstractCTAProducer {
    private static Logger log = LoggerFactory.getLogger(PredictorCTAProducer.class);

    @Value("${predictor.app.url.prefix}")
    private String predictorUrlPrefix;

    private final CTAType cTAType = PREDICTOR;

    @Override
    public CTA cta(CTAInfoHolder ctaInfoHolder, Map<String, String> ctaConfigMap, Client client) {
        if (APP.equals(client)) {
            return getPredictorCTA(ctaInfoHolder, ctaConfigMap);
        }
        return null;
    }

    private CTA getPredictorCTA(CTAInfoHolder ctaInfoHolder, Map<String, String> ctaConfiguration) {
        String key = ctaInfoHolder.ctaDbPropertyKey();
        String namespace = ctaInfoHolder.getCorrespondingEntity().name().toLowerCase();
        Long predictorPid = ctaInfoHolder.getCollegePredictorPid();
        if (predictorPid == null) {
            return null;
        }
        String name = ctaConfiguration.get(CTA.Constants.DISPLAY_NAME);
        String icon = ctaConfiguration
                .getOrDefault(CTA.Constants.ICON, ExploreConstants.CTA_LOGO_PLACEHOLDER);
        if (!checkIfNameExists(name, key, namespace)) {
            return null;
        }
        return CTA.builder()
                .logo(CommonUtil.getAbsoluteUrl(icon, ExploreConstants.CTA))
                .url(predictorUrlPrefix + predictorPid.toString())
                .type(CTAType.PREDICTOR)
                .label(name)
                .build();
    }
}


