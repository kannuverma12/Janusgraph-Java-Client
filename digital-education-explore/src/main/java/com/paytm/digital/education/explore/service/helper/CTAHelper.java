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
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Objects;

@Service
@AllArgsConstructor
public class CTAHelper {

    private FeeUrlGenerator feeUrlGenerator;
    private PropertyReader  propertyReader;

    public List<CTA> buildInstituteCTA(InstituteDetail instituteDetail, Client client) {

        Map<String, Object> logosPerCta = propertyReader
                .getPropertiesAsMapByKey(ExploreConstants.EXPLORE_COMPONENT,
                        EducationEntity.INSTITUTE.name().toLowerCase(),
                        ExploreConstants.CTA);

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

        ctas.add(getShortlistedCTA(instituteDetail.isShortlisted(), logosPerCta));
        return ctas;
    }

    public List<CTA> buildExamCTA(ExamDetail examDetail, Client client) {
        List<CTA> ctas = new ArrayList<>();
        Map<String, Object> logosPerCta = propertyReader
                .getPropertiesAsMapByKey(ExploreConstants.EXPLORE_COMPONENT,
                        EducationEntity.EXAM.name().toLowerCase(),
                        ExploreConstants.CTA);

        ctas.add(getLeadCTA(examDetail.isInterested(), false, client, logosPerCta));
        return ctas;
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

    private CTA getShortlistedCTA(boolean shortlisted, Map<String, Object> logosPerCta) {
        String shortListLabel = null;
        String relativeUrlKey = null;
        if (shortlisted) {
            shortListLabel = CTA.Constants.SHORTLISTED;
            relativeUrlKey = CTAType.SHORTLIST.name().toLowerCase() + ExploreConstants.SELECTED;
        } else {
            shortListLabel = CTA.Constants.SHORTLIST;
            relativeUrlKey = CTAType.SHORTLIST.name().toLowerCase();
        }
        String absoluteUrl = getAbsoluteLogoUrl(logosPerCta, relativeUrlKey);
        CTA shortListCta =
                CTA.builder().type(CTAType.SHORTLIST).label(shortListLabel)
                        .logo(absoluteUrl)
                        .build();
        return shortListCta;
    }

    private CTA getLeadCTA(boolean interested, boolean isThirdPartyClient, Client client,
            Map<String, Object> logosPerCta) {
        String leadLabel = "";
        String relativeUrlKey = null;
        if (!interested) {
            relativeUrlKey = CTAType.LEAD.name().toLowerCase();
            leadLabel = isThirdPartyClient ? CTA.Constants.GET_IN_TOUCH : CTA.Constants.GET_UPDATES;
        } else {
            relativeUrlKey = CTAType.LEAD.name().toLowerCase() + ExploreConstants.SELECTED;
            leadLabel = Client.APP.equals(client) && isThirdPartyClient
                    ? CTA.Constants.STOP_UPDATES : CTA.Constants.INTERESTED;
        }
        String absoluteUrl = getAbsoluteLogoUrl(logosPerCta, relativeUrlKey);
        CTA leadCta = CTA.builder().type(CTAType.LEAD).label(leadLabel)
                .logo(absoluteUrl).build();
        return leadCta;
    }

}
