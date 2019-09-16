package com.paytm.digital.education.coaching.ingestion.transformer;

import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingInstituteForm;
import com.paytm.digital.education.coaching.producer.model.embedded.KeyHighlight;
import com.paytm.digital.education.coaching.producer.model.embedded.OfficialAddress;
import com.paytm.digital.education.coaching.producer.model.request.CoachingInstituteDataRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class IngestorCoachingInstituteTransformer {

    public static CoachingInstituteDataRequest convert(final CoachingInstituteForm form) {

        final CoachingInstituteDataRequest request = CoachingInstituteDataRequest.builder()
                .instituteId(form.getInstituteId())
                .brandName(form.getBrandName())
                .aboutInstitute(form.getAboutInstitute())
                .logo(form.getLogo())
                .coverImage(form.getCoverImage())
                .highlights(buildHighlights(form))
                .streamIds(IngestorCommonTransformer.convertStringToListOfLong(
                        form.getStreamIds()))
                .examIds(IngestorCommonTransformer.convertStringToListOfLong(
                        form.getExamIds()))
                .priority(form.getGlobalPriority())
                .isEnabled(IngestorCommonTransformer.convertStringToBoolean(
                        form.getIsEnabled()))
                .courseTypes(IngestorCommonTransformer.convertCourseTypes(
                        form.getCourseTypes()))
                .courseLevels(IngestorCommonTransformer.convertCourseLevels(
                        form.getCourseLevel()))
                .officialAddress(convertAddress(form))
                .establishmentYear(form.getYearOfEstablishment())
                .brochureUrl(form.getBrochure())
                .build();

        log.info("CoachingInstituteDataRequest: {}", request);
        return request;
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
        final OfficialAddress officialAddress = OfficialAddress.builder()
                .addressLine1(form.getAddress())
                .city(form.getCity())
                .state(form.getState())
                .pincode(form.getPincode())
                .latitude(form.getLatitude())
                .longitude(form.getLongitude())
                .email(form.getEmailId())
                .phone(form.getPhoneNumber())
                .build();

        log.info("OfficialAddress: {}", officialAddress);
        return officialAddress;
    }
}
