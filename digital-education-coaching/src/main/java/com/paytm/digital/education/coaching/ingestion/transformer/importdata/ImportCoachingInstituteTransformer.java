package com.paytm.digital.education.coaching.ingestion.transformer.importdata;

import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingInstituteForm;
import com.paytm.digital.education.coaching.producer.model.embedded.Faq;
import com.paytm.digital.education.coaching.producer.model.embedded.KeyHighlight;
import com.paytm.digital.education.coaching.producer.model.embedded.OfficialAddress;
import com.paytm.digital.education.coaching.producer.model.request.CoachingInstituteDataRequest;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ImportCoachingInstituteTransformer {

    public static CoachingInstituteDataRequest convert(final CoachingInstituteForm form) {
        if (null == form) {
            return null;
        }
        return CoachingInstituteDataRequest.builder()
                .instituteId(form.getInstituteId())
                .brandName(form.getBrandName())
                .aboutInstitute(form.getAboutInstitute())
                .logo(form.getLogo())
                .coverImage(form.getCoverImage())
                .highlights(buildHighlights(form))
                .streamIds(ImportCommonTransformer.convertStringToListOfLong(
                        form.getStreamIds()))
                .examIds(ImportCommonTransformer.convertStringToListOfLong(
                        form.getExamIds()))
                .priority(form.getGlobalPriority())
                .isEnabled(ImportCommonTransformer.convertStringToBoolean(
                        form.getStatusActive()))
                .courseTypes(ImportCommonTransformer.convertCourseTypes(
                        form.getCourseTypes()))
                .officialAddress(convertAddress(form))
                .establishmentYear(form.getYearOfEstablishment())
                .brochureUrl(form.getBrochure())
                .faqs(buildFaqs(form))
                .moreInfo1(form.getMoreInfo1())
                .moreInfo2(form.getMoreInfo2())
                .moreInfo3(form.getMoreInfo3())
                .moreInfo4(form.getMoreInfo4())
                .courseLevels(ImportCommonTransformer.convertCourseLevels(
                        form.getLevelOfEducation()))
                .paytmMerchantId(form.getPaytmMerchantId())
                .build();
    }

    private static List<KeyHighlight> buildHighlights(final CoachingInstituteForm form) {
        final List<KeyHighlight> keyHighlights = new ArrayList<>();
        if (null != form) {
            keyHighlights.add(KeyHighlight.builder()
                    .key(form.getHighlightAttributeName1())
                    .value(form.getHighlightValue1())
                    .logo(form.getHighlightLogo1())
                    .build());
            keyHighlights.add(KeyHighlight.builder()
                    .key(form.getHighlightAttributeName2())
                    .value(form.getHighlightValue2())
                    .logo(form.getHighlightLogo2())
                    .build());
            keyHighlights.add(KeyHighlight.builder()
                    .key(form.getHighlightAttributeName3())
                    .value(form.getHighlightValue3())
                    .logo(form.getHighlightLogo3())
                    .build());
            keyHighlights.add(KeyHighlight.builder()
                    .key(form.getHighlightAttributeName4())
                    .value(form.getHighlightValue4())
                    .logo(form.getHighlightLogo4())
                    .build());
        }
        return keyHighlights;
    }

    private static OfficialAddress convertAddress(final CoachingInstituteForm form) {
        if (null == form) {
            return null;
        }
        return OfficialAddress.builder()
                .addressLine1(form.getAddress())
                .city(form.getCity())
                .state(form.getState())
                .pincode(form.getPincode())
                .latitude(form.getLatitude())
                .longitude(form.getLongitude())
                .email(form.getEmailId())
                .phone(form.getPhoneNumber())
                .build();
    }

    private static List<Faq> buildFaqs(final CoachingInstituteForm form) {

        final List<Faq> faqs = new ArrayList<>();

        if (!StringUtils.isEmpty(form.getFaq1()) && !StringUtils.isEmpty(form.getFaqAns1())) {
            faqs.add(new Faq(form.getFaq1(), form.getFaqAns1()));
        }
        if (!StringUtils.isEmpty(form.getFaq2()) && !StringUtils.isEmpty(form.getFaqAns2())) {
            faqs.add(new Faq(form.getFaq2(), form.getFaqAns2()));
        }
        if (!StringUtils.isEmpty(form.getFaq3()) && !StringUtils.isEmpty(form.getFaqAns3())) {
            faqs.add(new Faq(form.getFaq3(), form.getFaqAns3()));
        }
        return faqs;
    }
}
