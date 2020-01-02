package com.paytm.digital.education.explore.service.helper;

import com.google.common.collect.ImmutableMap;
import com.paytm.digital.education.constant.ExploreConstants;
import com.paytm.digital.education.database.entity.CTAConfig;
import com.paytm.digital.education.enums.Client;
import com.paytm.digital.education.enums.EducationEntity;
import com.paytm.digital.education.explore.enums.CTAType;
import com.paytm.digital.education.explore.response.dto.common.CTA;
import com.paytm.digital.education.explore.response.dto.detail.CTAInfoHolder;
import com.paytm.digital.education.explore.response.dto.detail.school.detail.SchoolDetail;
import com.paytm.digital.education.explore.service.external.FeeUrlGenerator;
import com.paytm.digital.education.explore.service.helper.cta.producer.BrochureCTAProducer;
import com.paytm.digital.education.explore.service.helper.cta.producer.CTAProducer;
import com.paytm.digital.education.explore.service.helper.cta.producer.CompareCTAProducer;
import com.paytm.digital.education.explore.service.helper.cta.producer.FeeCTAProducer;
import com.paytm.digital.education.explore.service.helper.cta.producer.FormCTAProducer;
import com.paytm.digital.education.explore.service.helper.cta.producer.LeadCTAProducer;
import com.paytm.digital.education.explore.service.helper.cta.producer.PredictorCTAProducer;
import com.paytm.digital.education.explore.service.helper.cta.producer.ShareCTAProducer;
import com.paytm.digital.education.explore.service.helper.cta.producer.ShortListCTAProducer;
import com.paytm.digital.education.property.reader.PropertyReader;
import com.paytm.digital.education.utility.CommonUtil;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.paytm.digital.education.constant.ExploreConstants.DIRECTORY_SEPARATOR_SLASH;
import static com.paytm.digital.education.constant.ExploreConstants.DISPLAY_NAME;
import static com.paytm.digital.education.constant.ExploreConstants.ICON;
import static com.paytm.digital.education.constant.ExploreConstants.WEB_FORM_URI_PREFIX;
import static com.paytm.digital.education.enums.Client.APP;
import static com.paytm.digital.education.explore.enums.CTAType.BROCHURE;
import static com.paytm.digital.education.explore.enums.CTAType.COMPARE;
import static com.paytm.digital.education.explore.enums.CTAType.FEE;
import static com.paytm.digital.education.explore.enums.CTAType.FORMS;
import static com.paytm.digital.education.explore.enums.CTAType.LEAD;
import static com.paytm.digital.education.explore.enums.CTAType.PREDICTOR;
import static com.paytm.digital.education.explore.enums.CTAType.SHARE;
import static com.paytm.digital.education.explore.enums.CTAType.SHORTLIST;
import static com.paytm.digital.education.explore.response.dto.common.CTA.Constants.ACTIVE_DISPLAY_NAME;
import static com.paytm.digital.education.explore.response.dto.common.CTA.Constants.ACTIVE_ICON;
import static com.paytm.digital.education.explore.response.dto.common.CTA.Constants.CLIENT;
import static com.paytm.digital.education.explore.response.dto.common.CTA.Constants.WEB;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
@RequiredArgsConstructor
public class CTAHelper {


    private final FeeUrlGenerator feeUrlGenerator;

    private final PropertyReader propertyReader;

    private final BrochureCTAProducer brochureCTAProducer;

    private final CompareCTAProducer compareCTAProducer;

    private final FeeCTAProducer feeCTAProducer;

    private final FormCTAProducer formCTAProducer;

    private final LeadCTAProducer leadCTAProducer;

    private final PredictorCTAProducer predictorCTAProducer;

    private final ShareCTAProducer shareCTAProducer;

    private final ShortListCTAProducer shortListCTAProducer;

    @Value("${forms.prefix.url}")
    private String formsUrlPrefix;

    @Value("${predictor.app.url.prefix}")
    private String predictorUrlPrefix;

    @Value("${forms.web.url.prefix}")
    private String formsWebUrlPrefix;

    private static Logger log = LoggerFactory.getLogger(CTAHelper.class);

    private final Map<CTAType, CTAProducer> ctaTypeCTAProducerMap = ImmutableMap.<CTAType, CTAProducer>builder()
            .put(BROCHURE, brochureCTAProducer)
            .put(COMPARE, compareCTAProducer)
            .put(FEE, feeCTAProducer)
            .put(FORMS, formCTAProducer)
            .put(LEAD, leadCTAProducer)
            .put(PREDICTOR, predictorCTAProducer)
            .put(SHARE, shareCTAProducer)
            .put(SHORTLIST, shortListCTAProducer)
            .build();

    public List<CTA> buildCTA(CTAInfoHolder ctaInfoHolder, Client client) {

        CTAConfig ctaConfig = getCTAConfig(ctaInfoHolder);
        CTAConfig finalCTAConfig = isEmpty(ctaConfig.getCtaTypes()) ? null : ctaConfig;

        String key = ctaInfoHolder.ctaDbPropertyKey();
        String namespace = ctaInfoHolder.getCorrespondingEntity().name().toLowerCase();
        Map<String, Object> ctaConfigurationMap = propertyReader.getPropertiesAsMapByKey(
                ExploreConstants.EXPLORE_COMPONENT, namespace, key);
        return finalCTAConfig.getCtaTypes()
                .stream()
                .map(ctaType -> ctaTypeCTAProducerMap
                        .get(ctaType)
                        .produceCTA(ctaInfoHolder, ctaConfigurationMap, client))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private CTAConfig getCTAConfig(CTAInfoHolder ctaInfoHolder) {
        EducationEntity educationEntity = ctaInfoHolder.getCorrespondingEntity();
        switch (educationEntity) {
            case SCHOOL:
                SchoolDetail school = (SchoolDetail) ctaInfoHolder;
                Long schoolId = school.getSchoolId();
        }
        return null;
    }

    public List<CTA> buildCTA(CTAInfoHolder ctaInfoHolder, Client client, String ignorable) {
        String key = ctaInfoHolder.ctaDbPropertyKey();
        String namespace = ctaInfoHolder.getCorrespondingEntity().name().toLowerCase();
        Map<String, Object> ctaConfigurationMap = propertyReader.getPropertiesAsMapByKey(
                ExploreConstants.EXPLORE_COMPONENT, namespace, key);
        List<CTA> ctas = new ArrayList<>();

        if (Objects.isNull(ctaConfigurationMap)) {
            log.warn("CTA config map not found for component {} namespace {} key {}",
                    ExploreConstants.EXPLORE_COMPONENT, namespace, key);
            return ctas;
        }
        log.info("CTA config map : {} , pid : {}, ", ctaConfigurationMap, ctaInfoHolder.getPid());

        if (Objects.nonNull(ctaInfoHolder.getPid())
                && checkIfCTAConfigExists(ctaConfigurationMap, FEE, namespace, key)) {
            CTA feeCta = getFeeCTA(ctaInfoHolder.getPid(), client,
                    (Map<String, String>) ctaConfigurationMap
                            .get(FEE.name().toLowerCase()), key, namespace);
            addCTAIfNotNull(ctas, feeCta);
        }

        /* Get Updates CTA */
        if (ctaInfoHolder.shouldHaveLeadCTA()
                && checkIfCTAConfigExists(ctaConfigurationMap, LEAD, namespace, key)) {
            CTA cta = getLeadCTA(ctaInfoHolder.isClient(),
                    (Map<String, String>) ctaConfigurationMap
                            .get(LEAD.name().toLowerCase()), key, namespace);
            addCTAIfNotNull(ctas, cta);
        }

        if (StringUtils.isNotBlank(ctaInfoHolder.getBrochureUrl())
                && checkIfCTAConfigExists(ctaConfigurationMap, BROCHURE, namespace, key)) {
            CTA cta = getBrochureCTA(ctaInfoHolder.getBrochureUrl(),
                    (Map<String, String>) ctaConfigurationMap
                            .get(BROCHURE.name().toLowerCase()), key, namespace);
            addCTAIfNotNull(ctas, cta);
        }

        if (ctaInfoHolder.hasCompareFeature()
                && !APP.equals(client)
                && checkIfCTAConfigExists(ctaConfigurationMap, COMPARE, namespace, key)) {
            CTA cta = getCompareCTA((Map<String, String>) ctaConfigurationMap
                    .get(COMPARE.name().toLowerCase()), key, namespace);
            addCTAIfNotNull(ctas, cta);
        }

        if (APP.equals(client)) {

            if (Objects.nonNull(ctaInfoHolder.getCollegePredictorPid())
                    && checkIfCTAConfigExists(ctaConfigurationMap, PREDICTOR, namespace,
                    key)) {
                CTA cta = getPredictorCTA(ctaInfoHolder.getCollegePredictorPid(),
                        (Map<String, String>) ctaConfigurationMap
                                .get(PREDICTOR.name().toLowerCase()), key, namespace);
                addCTAIfNotNull(ctas, cta);
            }

            /* Share CTA */
            if (ctaInfoHolder.hasShareFeature()
                    && checkIfCTAConfigExists(ctaConfigurationMap, CTAType.SHARE, namespace, key)) {
                CTA cta = getShareCTA((Map<String, String>) ctaConfigurationMap
                        .get(CTAType.SHARE.name().toLowerCase()), key, namespace);
                addCTAIfNotNull(ctas, cta);
            }
        }

        if (StringUtils.isNotBlank(ctaInfoHolder.getFormId())
                && checkIfCTAConfigExists(ctaConfigurationMap, FORMS, namespace, key)) {
            CTA cta = getFormsCTA(ctaInfoHolder.getFormId(),
                    (Map<String, String>) ctaConfigurationMap
                            .get(FORMS.name().toLowerCase()),
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
        if (!checkIfNameExists(name, PREDICTOR, key, namespace)) {
            return null;
        }
        return CTA.builder()
                .logo(CommonUtil.getAbsoluteUrl(icon, ExploreConstants.CTA))
                .url(predictorUrlPrefix + predictorId.toString())
                .type(PREDICTOR)
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
            if (!isEmpty(additionKeys) && additionKeys
                    .containsKey(WEB_FORM_URI_PREFIX)) {
                urlBuilder.append(additionKeys.get(WEB_FORM_URI_PREFIX).toString())
                        .append(DIRECTORY_SEPARATOR_SLASH);
            }
        }
        if (!checkIfNameExists(name, FORMS, key, namespace)) {
            return null;
        }
        String icon = ctaConfiguration
                .getOrDefault(CTA.Constants.ICON, ExploreConstants.CTA_LOGO_PLACEHOLDER);
        urlBuilder.append(formsId);
        return CTA.builder()
                .logo(CommonUtil.getAbsoluteUrl(icon, ExploreConstants.CTA))
                .url(urlBuilder.toString())
                .type(FORMS)
                .label(name)
                .build();
    }

    private CTA getFeeCTA(Long pid, Client client, Map<String, String> ctaConfiguration, String key,
            String namespace) {
        String name = ctaConfiguration.get(CTA.Constants.DISPLAY_NAME);
        String icon = ctaConfiguration
                .getOrDefault(CTA.Constants.ICON, ExploreConstants.CTA_LOGO_PLACEHOLDER);
        String feeUrl = feeUrlGenerator.generateUrl(pid, client);
        if (!checkIfNameExists(name, FEE, key, namespace)) {
            return null;
        }

        if (StringUtils.isBlank(feeUrl)) {
            log.error("Unable to build fee url for pid {}", pid);
            return null;
        }
        CTA cta = CTA.builder()
                .label(name)
                .logo(CommonUtil.getAbsoluteUrl(icon, ExploreConstants.CTA))
                .type(FEE)
                .url(feeUrl)
                .build();
        return cta;
    }

    private CTA getBrochureCTA(String brochureUrl, Map<String, String> ctaConfiguration, String key,
            String namespace) {
        String name = ctaConfiguration.get(DISPLAY_NAME);
        String relativeIcon =
                ctaConfiguration.getOrDefault(ICON, ExploreConstants.CTA_LOGO_PLACEHOLDER);
        if (!checkIfNameExists(name, COMPARE, key, namespace)) {
            return null;
        }
        return CTA.builder()
                .type(BROCHURE)
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

    private CTA getLeadCTA(boolean isThirdPartyClient, Map<String, String> ctaConfigurationMap,
            String key,
            String namespace) {
        String leadLabel = null;
        String activeLabel = null;
        if (isThirdPartyClient) {
            leadLabel = ctaConfigurationMap.get(DISPLAY_NAME + CLIENT);
            activeLabel = ctaConfigurationMap.get(ACTIVE_DISPLAY_NAME + CLIENT);
        } else {
            return null;
            //leadLabel = ctaConfigurationMap.get(DISPLAY_NAME);
            //activeLabel = ctaConfigurationMap.get(ACTIVE_DISPLAY_NAME);
        }
        String relativeUrl = ctaConfigurationMap
                .getOrDefault(ICON, ExploreConstants.CTA_LOGO_PLACEHOLDER);
        String activeRelativeUrl = ctaConfigurationMap
                .getOrDefault(ACTIVE_ICON, ExploreConstants.CTA_LOGO_PLACEHOLDER);
        if (!checkIfNameExists(leadLabel, activeLabel, LEAD, key, namespace)) {
            return null;
        }
        return CTA.builder()
                .type(LEAD)
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
        if (!checkIfNameExists(name, activeName, COMPARE, key, namespace)) {
            return null;
        }
        return CTA.builder()
                .type(COMPARE)
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
            log.warn("CTA name not found for {} key {} namespace {}", ctaType, key, namespace);
            return false;
        }
        return true;
    }

    private boolean checkIfNameExists(String name, String activeName, CTAType ctaType, String key,
            String namespace) {
        if (StringUtils.isBlank(activeName) || StringUtils.isBlank(name)) {
            log.warn("CTA name not found for {} key {} namespace {}", ctaType, key, namespace);
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
