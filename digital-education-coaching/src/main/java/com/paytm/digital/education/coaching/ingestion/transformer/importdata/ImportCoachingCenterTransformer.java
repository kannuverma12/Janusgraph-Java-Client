package com.paytm.digital.education.coaching.ingestion.transformer.importdata;

import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingCenterForm;
import com.paytm.digital.education.coaching.producer.model.embedded.OfficialAddress;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCenterDataRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImportCoachingCenterTransformer {

    public static CoachingCenterDataRequest convert(final CoachingCenterForm form) {
        if (null == form) {
            return null;
        }
        final CoachingCenterDataRequest request = CoachingCenterDataRequest.builder()
                .centerId(form.getCenterId())
                .instituteId(form.getInstituteId())
                .officialName(form.getOfficialName())
                .officialAddress(convertAddress(form))
                .courseTypes(ImportCommonTransformer.convertCourseTypes(
                        form.getCourseTypes()))
                .priority(form.getGlobalPriority())
                .isEnabled(ImportCommonTransformer.convertStringToBoolean(
                        form.getStatusActive()))
                .build();

        log.info("CoachingCenterDataRequest: {}", request);
        return request;
    }

    private static OfficialAddress convertAddress(final CoachingCenterForm form) {
        if (null == form) {
            return null;
        }
        final OfficialAddress officialAddress = OfficialAddress.builder()
                .addressLine1(form.getStreetAddress1())
                .addressLine2(form.getStreetAddress2())
                .addressLine3(form.getStreetAddress3())
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
