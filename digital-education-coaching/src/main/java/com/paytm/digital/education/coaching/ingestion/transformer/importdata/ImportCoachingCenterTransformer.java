package com.paytm.digital.education.coaching.ingestion.transformer.importdata;

import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingCenterForm;
import com.paytm.digital.education.coaching.producer.model.embedded.OfficialAddress;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCenterDataRequest;

import java.time.LocalTime;

public class ImportCoachingCenterTransformer {

    public static CoachingCenterDataRequest convert(final CoachingCenterForm form) {
        if (null == form) {
            return null;
        }
        return CoachingCenterDataRequest.builder()
                .centerId(form.getCenterId())
                .instituteId(form.getInstituteId())
                .officialName(form.getOfficialName())
                .officialAddress(convertAddress(form))
                .courseTypes(ImportCommonTransformer.convertCourseTypes(
                        form.getCourseTypes()))
                .openingTime(LocalTime.MAX)
                .closingTime(LocalTime.MAX)
                .centerImage(form.getCenterImage())
                .priority(form.getGlobalPriority())
                .isEnabled(ImportCommonTransformer.convertStringToBoolean(
                        form.getStatusActive()))
                .build();
    }

    private static OfficialAddress convertAddress(final CoachingCenterForm form) {
        if (null == form) {
            return null;
        }
        return OfficialAddress.builder()
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
    }
}
