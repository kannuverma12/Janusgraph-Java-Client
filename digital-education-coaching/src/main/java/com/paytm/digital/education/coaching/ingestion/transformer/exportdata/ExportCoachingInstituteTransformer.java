package com.paytm.digital.education.coaching.ingestion.transformer.exportdata;

import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingInstituteForm;
import com.paytm.digital.education.database.embedded.Faq;
import com.paytm.digital.education.database.embedded.KeyHighlight;
import com.paytm.digital.education.database.embedded.OfficialAddress;
import com.paytm.digital.education.database.entity.CoachingInstituteEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.EMPTY_STRING;

public class ExportCoachingInstituteTransformer {

    public static List<CoachingInstituteForm> convert(
            final List<CoachingInstituteEntity> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return new ArrayList<>();
        }
        return entityList.stream()
                .map(entity -> {
                    CoachingInstituteForm form = CoachingInstituteForm.builder()
                            .instituteId(entity.getInstituteId())
                            .brandName(entity.getBrandName())
                            .aboutInstitute(entity.getAboutInstitute())
                            .coverImage(entity.getCoverImage())
                            .logo(entity.getLogo())
                            .streamIds(entity.getStreams() == null
                                    ? EMPTY_STRING : StringUtils.join(entity.getStreams(), ","))
                            .examIds(entity.getExams() == null
                                    ? EMPTY_STRING : StringUtils.join(entity.getExams(), ","))
                            .courseTypes(entity.getCourseTypes() == null
                                    ? EMPTY_STRING : StringUtils.join(entity.getCourseTypes(), ","))
                            .yearOfEstablishment(entity.getEstablishmentYear())
                            .brochure(entity.getBrochure())
                            //TODO
                            .levelOfEducation(EMPTY_STRING)
                            //TODO
                            .moreInfo1(entity.getMoreInfo1())
                            .moreInfo2(entity.getMoreInfo2())
                            .moreInfo3(entity.getMoreInfo3())
                            .moreInfo4(entity.getMoreInfo4())
                            .statusActive(ExportCommonTransformer.convertBooleanToString(
                                    entity.getIsEnabled()))
                            .globalPriority(entity.getPriority())
                            .build();
                    fillAddress(form, entity.getOfficialAddress());
                    fillFaqs(form, entity.getFaqs());
                    fillHighlights(form, entity.getKeyHighlights());
                    return form;
                })
                .collect(Collectors.toList());
    }

    private static void fillAddress(final CoachingInstituteForm form,
            final OfficialAddress officialAddress) {
        if (null == officialAddress) {
            return;
        }
        form.setAddress(officialAddress.getAddressLine1());
        form.setCity(officialAddress.getCity());
        form.setState(officialAddress.getState());
        form.setPincode(officialAddress.getPincode());
        form.setLatitude(officialAddress.getLatitude());
        form.setLongitude(officialAddress.getLongitude());
        form.setEmailId(officialAddress.getEmail());
        form.setPhoneNumber(officialAddress.getPhone());
    }

    private static void fillFaqs(final CoachingInstituteForm form, final List<Faq> faqs) {
        if (CollectionUtils.isEmpty(faqs)) {
            return;
        }

        if (null != faqs.get(0)) {
            form.setFaq1(faqs.get(0).getQuestion());
            form.setFaqAns1(faqs.get(0).getAnswers());
        }
        if (faqs.size() > 1 && null != faqs.get(1)) {
            form.setFaq2(faqs.get(1).getQuestion());
            form.setFaqAns2(faqs.get(1).getAnswers());
        }
        if (faqs.size() > 2 && null != faqs.get(2)) {
            form.setFaq3(faqs.get(2).getQuestion());
            form.setFaqAns3(faqs.get(2).getAnswers());
        }
    }

    private static void fillHighlights(final CoachingInstituteForm form,
            final List<KeyHighlight> keyHighlights) {
        if (CollectionUtils.isEmpty(keyHighlights)) {
            return;
        }

        if (null != keyHighlights.get(0)) {
            form.setHighlightAttributeName1(keyHighlights.get(0).getKey());
            form.setHighlightLogo1(keyHighlights.get(0).getLogo());
            form.setHighlightValue1(keyHighlights.get(0).getValue());
        }
        if (keyHighlights.size() > 1 && null != keyHighlights.get(1)) {
            form.setHighlightAttributeName2(keyHighlights.get(1).getKey());
            form.setHighlightLogo2(keyHighlights.get(1).getLogo());
            form.setHighlightValue2(keyHighlights.get(1).getValue());
        }
        if (keyHighlights.size() > 2 && null != keyHighlights.get(2)) {
            form.setHighlightAttributeName3(keyHighlights.get(2).getKey());
            form.setHighlightLogo3(keyHighlights.get(2).getLogo());
            form.setHighlightValue3(keyHighlights.get(2).getValue());
        }
        if (keyHighlights.size() > 3 && null != keyHighlights.get(3)) {
            form.setHighlightAttributeName4(keyHighlights.get(3).getKey());
            form.setHighlightLogo4(keyHighlights.get(3).getLogo());
            form.setHighlightValue4(keyHighlights.get(3).getValue());
        }
    }
}
