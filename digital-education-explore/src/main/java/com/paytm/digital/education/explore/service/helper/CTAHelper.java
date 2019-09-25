package com.paytm.digital.education.explore.service.helper;

import static com.paytm.digital.education.explore.enums.Client.APP;
import static com.paytm.digital.education.explore.enums.EducationEntity.EXAM;
import static com.paytm.digital.education.explore.enums.EducationEntity.SCHOOL;
import static com.paytm.digital.education.explore.response.dto.common.CTA.Constants.SHORTLIST;
import static com.paytm.digital.education.explore.response.dto.common.CTA.Constants.SHORTLISTED_EXAM_APP;
import static com.paytm.digital.education.explore.response.dto.common.CTA.Constants.SHORTLISTED_SCHOOL_APP;
import static com.paytm.digital.education.explore.response.dto.common.CTA.Constants.SHORTLIST_APP;
import static com.paytm.digital.education.explore.response.dto.common.CTA.Constants.SHORTLIST_EXAM_APP;
import static com.paytm.digital.education.explore.response.dto.common.CTA.Constants.SHORTLIST_SCHOOL_APP;

import com.paytm.digital.education.explore.constants.ExploreConstants;
import com.paytm.digital.education.explore.enums.CTAType;
import com.paytm.digital.education.explore.enums.Client;
import com.paytm.digital.education.explore.response.dto.common.CTA;
import com.paytm.digital.education.explore.response.dto.detail.CTAInfoHolder;
import com.paytm.digital.education.explore.service.external.FeeUrlGenerator;
import com.paytm.digital.education.explore.utility.CommonUtil;
import com.paytm.digital.education.property.reader.PropertyReader;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    public List<CTA> buildCTA(CTAInfoHolder ctaInfoHolder, Client client) {
        // Logos are not required for web.
        Map<String, Object> logosPerCta = null;
        if (APP.equals(client)) {
            logosPerCta = propertyReader.getPropertiesAsMapByKey(
                    ExploreConstants.EXPLORE_COMPONENT,
                    ctaInfoHolder.getCorrespondingEntity().name().toLowerCase(),
                    ctaInfoHolder.ctaDbPropertyKey());
        }

        if (CollectionUtils.isEmpty(logosPerCta)) {
            logosPerCta = Collections.emptyMap();
        }

        List<CTA> ctas = new ArrayList<>();

        if (Objects.nonNull(ctaInfoHolder.getPid())) {
            CTA feeCta = getFeeCTA(ctaInfoHolder.getPid(), client, logosPerCta);
            if (Objects.nonNull(feeCta)) {
                ctas.add(feeCta);
            }
        }

        if (ctaInfoHolder.shouldHaveLeadCTA()) {
            ctas.add(getLeadCTA(ctaInfoHolder.isClient(), logosPerCta));
        }

        if (StringUtils.isNotBlank(ctaInfoHolder.getBrochureUrl())) {
            ctas.add(getBrochureCTA(ctaInfoHolder.getBrochureUrl(), logosPerCta));
        }

        if (ctaInfoHolder.hasCompareFeature()) {
            if (!APP.equals(client)) {
                ctas.add(getCompareCTA(logosPerCta));
            }
        }

        if (APP.equals(client) && Objects.nonNull(ctaInfoHolder.getCollegePredictorPid())) {
            ctas.add(getPredictorCTA(ctaInfoHolder.getCollegePredictorPid(), logosPerCta));
        }

        addApplyNowCTAIfRequired(ctas, ctaInfoHolder, client, logosPerCta);

        if (ctaInfoHolder.hasShortListFeature()) {
            ctas.add(getShortlistCTA(ctaInfoHolder, logosPerCta, client));
        }

        return ctas;
    }

    private void addApplyNowCTAIfRequired(
            List<CTA> ctas, CTAInfoHolder ctaDetail, Client client, Map<String, Object> logosPerCta) {
        if (!ctaDetail.shouldHaveApplyNowCTA()) {
            return;
        }

        if (APP.equals(client) && StringUtils.isNotBlank(ctaDetail.getFormId())) {
            ctas.add(getFormsCTA(ctaDetail.getFormId(), logosPerCta));
        }
    }

    private CTA getPredictorCTA(Long predictorId, Map<String, Object> logosPerCta) {
        return CTA.builder()
                .logo(getAbsoluteLogoUrl(logosPerCta, CTAType.PREDICTOR.name().toLowerCase()))
                .url(predictorUrlPrefix + predictorId.toString())
                .type(CTAType.PREDICTOR)
                .label(CTA.Constants.PREDICT_COLLEGE)
                .build();
    }

    private CTA getFormsCTA(String formsUrl, Map<String, Object> logosPerCta) {
        return CTA.builder()
                .logo(getAbsoluteLogoUrl(logosPerCta, CTAType.FORMS.name().toLowerCase()))
                .url(formsUrlPrefix + formsUrl)
                .type(CTAType.FORMS)
                .label(CTA.Constants.APPLY).build();
    }

    private CTA getFeeCTA(Long pid, Client client, Map<String, Object> logosPerCta) {
        String feeUrl = feeUrlGenerator.generateUrl(pid, client);
        if (StringUtils.isNotBlank(feeUrl)) {
            CTA cta = CTA.builder().label(CTA.Constants.PAY_FEE)
                    .logo(getAbsoluteLogoUrl(logosPerCta, CTAType.FEE.name().toLowerCase()))
                    .type(CTAType.FEE)
                    .url(feeUrl).build();
            return cta;
        }
        return null;
    }

    private String getAbsoluteLogoUrl(Map<String, Object> logoMap, String relativeUrlKey) {
        String relativeUrl = ExploreConstants.CTA_LOGO_PLACEHOLDER;
        if (!CollectionUtils.isEmpty(logoMap) && logoMap.containsKey(relativeUrlKey)) {
            relativeUrl = (String) logoMap.get(relativeUrlKey);
        }
        return CommonUtil.getAbsoluteUrl(relativeUrl, ExploreConstants.CTA);
    }

    private CTA getBrochureCTA(String brochureUrl, Map<String, Object> logosPerCta) {
        CTA brochureCta =
                CTA.builder().type(CTAType.BROCHURE).label(CTA.Constants.BROCHURE)
                        .logo(getAbsoluteLogoUrl(logosPerCta,
                                CTAType.BROCHURE.name().toLowerCase()))
                        .url(brochureUrl).build();
        return brochureCta;
    }

    private String getAppShortListLabel(CTAInfoHolder ctaInfoHolder) {
        if (SCHOOL.equals(ctaInfoHolder.getCorrespondingEntity())) {
            return SHORTLIST_SCHOOL_APP;
        } else if (EXAM.equals(ctaInfoHolder.getCorrespondingEntity())) {
            return SHORTLIST_EXAM_APP;
        } else {
            return SHORTLIST_APP;
        }
    }

    private String getAppShortListedLabel(CTAInfoHolder ctaInfoHolder) {
        if (SCHOOL.equals(ctaInfoHolder.getCorrespondingEntity())) {
            return SHORTLISTED_SCHOOL_APP;
        } else if (EXAM.equals(ctaInfoHolder.getCorrespondingEntity())) {
            return SHORTLISTED_EXAM_APP;
        } else {
            return SHORTLIST_APP;
        }
    }

    private CTA getShortlistCTA(CTAInfoHolder ctaInfoHolder, Map<String, Object> logosPerCta,
            Client client) {
        String shortListLabel = APP.equals(client)
                ? getAppShortListLabel(ctaInfoHolder) : SHORTLIST;
        String activeLabel = APP.equals(client)
                ? getAppShortListedLabel(ctaInfoHolder) : CTA.Constants.SHORTLISTED;
        String absoluteUrl =
                getAbsoluteLogoUrl(logosPerCta, CTAType.SHORTLIST.name().toLowerCase());
        String absoluteActiveLogoUrl = getAbsoluteLogoUrl(logosPerCta,
                CTAType.SHORTLIST.name().toLowerCase() + ExploreConstants.SELECTED);
        CTA shortListCta =
                CTA.builder().type(CTAType.SHORTLIST)
                        .label(shortListLabel)
                        .activeLogo(absoluteActiveLogoUrl)
                        .activeText(activeLabel)
                        .logo(absoluteUrl)
                        .build();
        return shortListCta;
    }

    private CTA getLeadCTA(boolean isThirdPartyClient, Map<String, Object> logosPerCta) {
        String leadLabel = null;
        String activeLabel = null;
        if (isThirdPartyClient) {
            leadLabel = CTA.Constants.GET_IN_TOUCH;
            activeLabel = CTA.Constants.GET_IN_TOUCH;
        } else {
            leadLabel = CTA.Constants.GET_UPDATES;
            activeLabel = CTA.Constants.INQUIRY_SENT;
        }
        String absoluteUrl = getAbsoluteLogoUrl(logosPerCta, CTAType.LEAD.name().toLowerCase());
        String activeLogoUrl = getAbsoluteLogoUrl(logosPerCta,
                CTAType.LEAD.name().toLowerCase() + ExploreConstants.SELECTED);
        CTA leadCta = CTA.builder().type(CTAType.LEAD).label(leadLabel)
                .activeText(activeLabel)
                .activeLogo(activeLogoUrl)
                .logo(absoluteUrl).build();
        return leadCta;
    }

    private CTA getCompareCTA(Map<String, Object> logosPerCta) {
        String absoluteUrl = getAbsoluteLogoUrl(logosPerCta, CTAType.COMPARE.name().toLowerCase());
        return CTA.builder().type(CTAType.COMPARE).label(CTA.Constants.COMPARE).logo(absoluteUrl)
                .build();
    }

}
