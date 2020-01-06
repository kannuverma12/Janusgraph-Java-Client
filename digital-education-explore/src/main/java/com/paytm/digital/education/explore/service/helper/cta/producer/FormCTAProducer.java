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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Map;

import static com.paytm.digital.education.constant.ExploreConstants.DIRECTORY_SEPARATOR_SLASH;
import static com.paytm.digital.education.constant.ExploreConstants.DISPLAY_NAME;
import static com.paytm.digital.education.constant.ExploreConstants.WEB_FORM_URI_PREFIX;
import static com.paytm.digital.education.enums.Client.APP;
import static com.paytm.digital.education.explore.enums.CTAType.FORMS;
import static com.paytm.digital.education.explore.response.dto.common.CTA.Constants.WEB;

@Service
@Getter
public class FormCTAProducer extends AbstractCTAProducer {
    private static Logger log = LoggerFactory.getLogger(FormCTAProducer.class);

    @Value("${forms.prefix.url}")
    private String formsUrlPrefix;

    @Value("${predictor.app.url.prefix}")
    private String predictorUrlPrefix;

    @Value("${forms.web.url.prefix}")
    private String formsWebUrlPrefix;

    @Accessors(fluent = true)
    private final CTAType ctaType = FORMS;

    @Override
    public CTA cta(CTAInfoHolder ctaInfoHolder, Map<String, String> ctaConfiguration, Client client) {
        return getFormsCTA(ctaInfoHolder, ctaConfiguration, client);
    }

    private CTA getFormsCTA(CTAInfoHolder ctaInfoHolder, Map<String, String> ctaConfiguration, Client client) {
        String formsId = ctaInfoHolder.getFormId();
        if (formsId == null) {
            return null;
        }
        String key = ctaInfoHolder.ctaDbPropertyKey();
        String namespace = ctaInfoHolder.getCorrespondingEntity().name().toLowerCase();
        Map<String, Object> additionKeys = ctaInfoHolder.getAdditionalProperties();
        String name = null;
        StringBuilder urlBuilder = null;
        if (APP.equals(client)) {
            urlBuilder = new StringBuilder(formsUrlPrefix);
            name = ctaConfiguration.get(DISPLAY_NAME);
        } else {
            name = ctaConfiguration.get(DISPLAY_NAME + WEB);
            urlBuilder = new StringBuilder(formsWebUrlPrefix);
            if (!CollectionUtils.isEmpty(additionKeys) && additionKeys
                    .containsKey(WEB_FORM_URI_PREFIX)) {
                urlBuilder.append(additionKeys.get(WEB_FORM_URI_PREFIX).toString())
                        .append(DIRECTORY_SEPARATOR_SLASH);
            }
        }
        if (!checkIfNameExists(name, key, namespace)) {
            return null;
        }
        String icon = ctaConfiguration
                .getOrDefault(CTA.Constants.ICON, ExploreConstants.CTA_LOGO_PLACEHOLDER);
        urlBuilder.append(formsId);
        return CTA.builder()
                .logo(CommonUtil.getAbsoluteUrl(icon, ExploreConstants.CTA))
                .url(urlBuilder.toString())
                .type(CTAType.FORMS)
                .label(name)
                .build();
    }
}


