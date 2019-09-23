package com.paytm.digital.education.coaching.ingestion.transformer.exportdata;

import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingCenterForm;
import com.paytm.digital.education.database.embedded.OfficialAddress;
import com.paytm.digital.education.database.entity.CoachingCenterEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.EMPTY_STRING;

public class ExportCoachingCenterTransformer {

    public static List<CoachingCenterForm> convert(final List<CoachingCenterEntity> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return new ArrayList<>();
        }
        return entityList.stream()
                .map(entity -> {
                    final CoachingCenterForm form = CoachingCenterForm.builder()
                            .centerId(entity.getCenterId())
                            .instituteId(entity.getInstituteId())
                            .officialName(entity.getOfficialName())
                            .courseTypes(StringUtils.join(entity.getCourseTypes(), ","))
                            .openingTime(entity.getOpeningTime().toString())
                            .closingTime(entity.getClosingTime().toString())
                            .centerImage(entity.getCenterImage())
                            .globalPriority(entity.getPriority())
                            .statusActive(ExportCommonTransformer.convertBooleanToString(
                                    entity.getIsEnabled()))
                            .build();
                    fillAddress(form, entity.getOfficialAddress());
                    return form;
                })
                .collect(Collectors.toList());
    }

    private static void fillAddress(final CoachingCenterForm form,
            final OfficialAddress officialAddress) {
        if (null == officialAddress) {
            return;
        }
        form.setStreetAddress1(officialAddress.getAddressLine1());
        form.setStreetAddress2(officialAddress.getAddressLine2());
        form.setStreetAddress3(officialAddress.getAddressLine3());
        form.setCity(officialAddress.getCity());
        form.setState(officialAddress.getState());
        form.setPincode(officialAddress.getPincode());
        form.setLatitude(officialAddress.getLatitude());
        form.setLongitude(officialAddress.getLongitude());
        form.setEmailId(officialAddress.getEmail());
        form.setPhoneNumber(officialAddress.getPhone());
    }
}
