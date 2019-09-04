package com.paytm.digital.education.explore.service.helper;

import com.paytm.digital.education.explore.constants.ExploreConstants;
import com.paytm.digital.education.explore.enums.CTAType;
import com.paytm.digital.education.explore.enums.Client;
import com.paytm.digital.education.explore.enums.EducationEntity;
import com.paytm.digital.education.explore.response.dto.common.CTA;
import com.paytm.digital.education.explore.response.dto.detail.ExamDetail;
import com.paytm.digital.education.explore.response.dto.detail.InstituteDetail;
import com.paytm.digital.education.explore.service.external.FeeUrlGenerator;
import com.paytm.digital.education.explore.utility.CommonUtil;
import com.paytm.digital.education.property.reader.PropertyReader;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
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


    public List<CTA> buildInstituteCTA(InstituteDetail instituteDetail, Client client) {

        Map<String, Object> logosPerCta = null;
        // Logos are not required for web.
        if (Client.APP.equals(client)) {
            logosPerCta = propertyReader
                    .getPropertiesAsMapByKey(ExploreConstants.EXPLORE_COMPONENT,
                            EducationEntity.INSTITUTE.name().toLowerCase(),
                            ExploreConstants.CTA);
        }

        List<CTA> ctas = new ArrayList<>();

        if (Objects.nonNull(instituteDetail.getPid())) {
            CTA feeCta = getFeeCTA(instituteDetail.getPid(), client, logosPerCta);
            if (Objects.nonNull(feeCta)) {
                ctas.add(feeCta);
            }
        }

        ctas.add(getLeadCTA(instituteDetail.isInterested(), instituteDetail.isClient(), client,
                logosPerCta));

        if (StringUtils.isNotBlank(instituteDetail.getBrochureUrl())) {
            ctas.add(getBrochureCTA(instituteDetail.getBrochureUrl(), logosPerCta));
        }

        ctas.add(getShortlistedCTA(instituteDetail.isShortlisted(), logosPerCta, client));

        if (!Client.APP.equals(client)) {
            ctas.add(getCompareCTA(logosPerCta));
        }

        return ctas;
    }

    public List<CTA> buildExamCTA(ExamDetail examDetail, Client client) {
        List<CTA> ctas = new ArrayList<>();
        Map<String, Object> logosPerCta = propertyReader
                .getPropertiesAsMapByKey(ExploreConstants.EXPLORE_COMPONENT,
                        EducationEntity.EXAM.name().toLowerCase(),
                        ExploreConstants.CTA);

        if (Client.APP.equals(client) && StringUtils.isNotBlank(examDetail.getFormId())) {
            ctas.add(getFormsCTA(examDetail.getFormId(), logosPerCta));
        }

        if (Client.APP.equals(client) && Objects.nonNull(examDetail.getCollegePredictorPid())) {
            ctas.add(getPredictorCTA(examDetail.getCollegePredictorPid(), logosPerCta));
        }

        ctas.add(getLeadCTA(examDetail.isInterested(), false, client, logosPerCta));
        return ctas;
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

    private CTA getShortlistedCTA(boolean shortlisted, Map<String, Object> logosPerCta,
            Client client) {
        String shortListLabel = Client.APP.equals(client)
                ? CTA.Constants.SHORTLIST_APP : CTA.Constants.SHORTLIST;
        String activeLabel = Client.APP.equals(client)
                ? CTA.Constants.SHORTLISTED_APP : CTA.Constants.SHORTLISTED;
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

    private CTA getLeadCTA(boolean interested, boolean isThirdPartyClient, Client client,
            Map<String, Object> logosPerCta) {
        String leadLabel = null;
        String activeLabel = null;
        if (isThirdPartyClient) {
            leadLabel = CTA.Constants.GET_IN_TOUCH;
            activeLabel = CTA.Constants.INQUIRY_SENT;
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
