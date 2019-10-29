package com.paytm.digital.education.explore.service.helper;

import com.paytm.digital.education.constant.ExploreConstants;
import com.paytm.digital.education.enums.Client;
import com.paytm.digital.education.explore.enums.CTAType;
import com.paytm.digital.education.explore.response.dto.common.CTA;
import com.paytm.digital.education.explore.response.dto.detail.CTAInfoHolder;
import com.paytm.digital.education.explore.service.external.FeeUrlGenerator;
import com.paytm.digital.education.property.reader.PropertyReader;
import com.paytm.digital.education.utility.CommonUtil;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.paytm.digital.education.constant.ExploreConstants.DIRECTORY_SEPARATOR_SLASH;
import static com.paytm.digital.education.constant.ExploreConstants.DISPLAY_NAME;
import static com.paytm.digital.education.constant.ExploreConstants.ICON;
import static com.paytm.digital.education.constant.ExploreConstants.WEB_FORM_URI_PREFIX;
import static com.paytm.digital.education.enums.Client.APP;
import static com.paytm.digital.education.explore.response.dto.common.CTA.Constants.ACTIVE_DISPLAY_NAME;
import static com.paytm.digital.education.explore.response.dto.common.CTA.Constants.ACTIVE_ICON;
import static com.paytm.digital.education.explore.response.dto.common.CTA.Constants.CLIENT;
import static com.paytm.digital.education.explore.response.dto.common.CTA.Constants.WEB;

@Service
public class CTAHelper {

    @Autowired
    private FeeUrlGenerator feeUrlGenerator;

    @Autowired
    private PropertyReader propertyReader;

    @Value("${forms.prefix.url}")
    private String formsUrlPrefix;

    @Value("${predictor.app.url.prefix}")
    private String predictorUrlPrefix;

    @Value("${forms.web.url.prefix}")
    private String formsWebUrlPrefix;

    private static Logger log = LoggerFactory.getLogger(CTAHelper.class);

    public List<CTA> buildCTA(CTAInfoHolder ctaInfoHolder, Client client) {
        String key = ctaInfoHolder.ctaDbPropertyKey();
        String namespace = ctaInfoHolder.getCorrespondingEntity().name().toLowerCase();
        Map<String, Object> ctaConfigurationMap = propertyReader.getPropertiesAsMapByKey(
                ExploreConstants.EXPLORE_COMPONENT, namespace, key);
        List<CTA> ctas = new ArrayList<>();

        if (Objects.isNull(ctaConfigurationMap)) {
            log.error("CTA config map not found for component {} namespace {} key {}",
                    ExploreConstants.EXPLORE_COMPONENT, namespace, key);
            return ctas;
        }

        if (Objects.nonNull(ctaInfoHolder.getPid())
                && checkIfCTAConfigExists(ctaConfigurationMap, CTAType.FEE, namespace, key)) {
            CTA feeCta = getFeeCTA(ctaInfoHolder.getPid(), client,
                    (Map<String, String>) ctaConfigurationMap
                            .get(CTAType.FEE.name().toLowerCase()), key, namespace);
            addCTAIfNotNull(ctas, feeCta);
        }

        if (ctaInfoHolder.shouldHaveLeadCTA()
                && checkIfCTAConfigExists(ctaConfigurationMap, CTAType.LEAD, namespace, key)) {
            CTA cta = getLeadCTA(ctaInfoHolder.isClient(),
                    (Map<String, String>) ctaConfigurationMap
                            .get(CTAType.LEAD.name().toLowerCase()), key, namespace);
            addCTAIfNotNull(ctas, cta);
        }

        if (StringUtils.isNotBlank(ctaInfoHolder.getBrochureUrl())
                && checkIfCTAConfigExists(ctaConfigurationMap, CTAType.BROCHURE, namespace, key)) {
            CTA cta = getBrochureCTA(ctaInfoHolder.getBrochureUrl(),
                    (Map<String, String>) ctaConfigurationMap
                            .get(CTAType.BROCHURE.name().toLowerCase()), key, namespace);
            addCTAIfNotNull(ctas, cta);
        }

        if (ctaInfoHolder.hasCompareFeature()
                && !APP.equals(client)
                && checkIfCTAConfigExists(ctaConfigurationMap, CTAType.COMPARE, namespace, key)) {
            CTA cta = getCompareCTA((Map<String, String>) ctaConfigurationMap
                    .get(CTAType.COMPARE.name().toLowerCase()), key, namespace);
            addCTAIfNotNull(ctas, cta);
        }

        if (APP.equals(client)) {

            if (Objects.nonNull(ctaInfoHolder.getCollegePredictorPid())
                    && checkIfCTAConfigExists(ctaConfigurationMap, CTAType.PREDICTOR, namespace,
                    key)) {
                CTA cta = getPredictorCTA(ctaInfoHolder.getCollegePredictorPid(),
                        (Map<String, String>) ctaConfigurationMap
                                .get(CTAType.PREDICTOR.name().toLowerCase()), key, namespace);
                addCTAIfNotNull(ctas, cta);
            }

            if (ctaInfoHolder.hasShareFeature()
                    && checkIfCTAConfigExists(ctaConfigurationMap, CTAType.SHARE, namespace, key)) {
                CTA cta = getShareCTA((Map<String, String>) ctaConfigurationMap
                        .get(CTAType.SHARE.name().toLowerCase()), key, namespace);
                addCTAIfNotNull(ctas, cta);
            }
        }

        if (StringUtils.isNotBlank(ctaInfoHolder.getFormId())
                && checkIfCTAConfigExists(ctaConfigurationMap, CTAType.FORMS, namespace, key)) {
            CTA cta = getFormsCTA(ctaInfoHolder.getFormId(),
                    (Map<String, String>) ctaConfigurationMap
                            .get(CTAType.FORMS.name().toLowerCase()),
                    ctaInfoHolder.getAdditionalProperties(), client, key, namespace);
            addCTAIfNotNull(ctas, cta);
        }

        if (ctaInfoHolder.hasShortListFeature()
                && checkIfCTAConfigExists(ctaConfigurationMap, CTAType.SHORTLIST, namespace, key)) {
            CTA cta = getShortlistCTA((Map<String, String>) ctaConfigurationMap
                    .get(CTAType.SHORTLIST.name().toLowerCase()), client, key, namespace);
            addCTAIfNotNull(ctas, cta);
        }

        return ctas;
    }

    private CTA getPredictorCTA(Long predictorId, Map<String, String> ctaConfiguration, String key,
            String namespace) {
        String name = ctaConfiguration.get(CTA.Constants.DISPLAY_NAME);
        String icon = ctaConfiguration
                .getOrDefault(CTA.Constants.ICON, ExploreConstants.CTA_LOGO_PLACEHOLDER);
        if (!checkIfNameExists(name, CTAType.PREDICTOR, key, namespace)) {
            return null;
        }
        return CTA.builder()
                .logo(CommonUtil.getAbsoluteUrl(icon, ExploreConstants.CTA))
                .url(predictorUrlPrefix + predictorId.toString())
                .type(CTAType.PREDICTOR)
                .label(name)
                .build();
    }

    private CTA getFormsCTA(String formsId, Map<String, String> ctaConfiguration,
            Map<String, Object> additionKeys, Client client, String key,
            String namespace) {
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
        if (!checkIfNameExists(name, CTAType.FORMS, key, namespace)) {
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

    private CTA getFeeCTA(Long pid, Client client, Map<String, String> ctaConfiguration, String key,
            String namespace) {
        String name = ctaConfiguration.get(CTA.Constants.DISPLAY_NAME);
        String icon = ctaConfiguration
                .getOrDefault(CTA.Constants.ICON, ExploreConstants.CTA_LOGO_PLACEHOLDER);
        String feeUrl = feeUrlGenerator.generateUrl(pid, client);
        if (!checkIfNameExists(name, CTAType.FEE, key, namespace)) {
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

    private CTA getBrochureCTA(String brochureUrl, Map<String, String> ctaConfiguration, String key,
            String namespace) {
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

    private CTA getShortlistCTA(Map<String, String> ctaConfiguration, Client client, String key,
            String namespace) {
        String shortListLabel = null;
        String activeLabel = null;
        if (APP.equals(client)) {
            shortListLabel = ctaConfiguration.get(DISPLAY_NAME);
            activeLabel = ctaConfiguration.get(DISPLAY_NAME);
        } else {
            shortListLabel = ctaConfiguration.get(DISPLAY_NAME + WEB);
            activeLabel = ctaConfiguration.get(DISPLAY_NAME + WEB);
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

    private CTA getLeadCTA(boolean isThirdPartyClient, Map<String, String> ctaConfigurationMap,
            String key,
            String namespace) {
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

    private CTA getCompareCTA(Map<String, String> ctaConfiguration, String key,
            String namespace) {
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

    private CTA getShareCTA(Map<String, String> ctaConfiguration, String key,
            String namespace) {
        String name = ctaConfiguration.get(DISPLAY_NAME);
        String relativeIconUrl =
                ctaConfiguration.getOrDefault(ICON, ExploreConstants.CTA_LOGO_PLACEHOLDER);
        if (!checkIfNameExists(name, CTAType.SHARE, key, namespace)) {
            return null;
        }
        return CTA.builder()
                .type(CTAType.SHARE)
                .label(name)
                .logo(CommonUtil.getAbsoluteUrl(relativeIconUrl, ExploreConstants.CTA))
                .build();
    }

    private boolean checkIfNameExists(String name, CTAType ctaType, String key,
            String namespace) {
        if (StringUtils.isBlank(name)) {
            log.error("CTA name not found for {} key {} namespace {}", ctaType, key, namespace);
            return false;
        }
        return true;
    }

    private boolean checkIfNameExists(String name, String activeName, CTAType ctaType, String key,
            String namespace) {
        if (StringUtils.isBlank(activeName) || StringUtils.isBlank(name)) {
            log.error("CTA name not found for {} key {} namespace {}", ctaType, key, namespace);
            return false;
        }
        return true;
    }

    private void addCTAIfNotNull(List<CTA> ctas, CTA cta) {
        if (Objects.nonNull(cta)) {
            ctas.add(cta);
        }
    }

    private boolean checkIfCTAConfigExists(Map<String, Object> ctaConfigurationMap,
            CTAType ctaType, String namespace, String key) {
        if (ctaConfigurationMap.containsKey(ctaType.name().toLowerCase())) {
            return true;
        }
        log.error("Cta config not found for {} component : {}, key : {} namespace :{}, ", ctaType,
                ExploreConstants.EXPLORE_COMPONENT, key, namespace);
        return false;
    }

}
