package com.paytm.digital.education.explore.service.helper;

import com.google.common.collect.Lists;
import com.paytm.digital.education.explore.response.dto.detail.school.detail.SchoolDetail;
import lombok.experimental.UtilityClass;
import org.springframework.util.CollectionUtils;

import java.util.LinkedHashSet;
import java.util.Objects;

@UtilityClass
public class SchoolDetailsResponseHelper {
    public void pruneDuplicateDataInSchoolDetail(SchoolDetail schoolDetail) {
        if (Objects.isNull(schoolDetail)) {
            return;
        }
        pruneShifts(schoolDetail);
        pruneFees(schoolDetail);
    }

    private void pruneShifts(SchoolDetail schoolDetail) {
        if (!CollectionUtils.isEmpty(schoolDetail.getShiftDetailsList())) {
            schoolDetail.setShiftDetailsList(
                    Lists.newArrayList(new LinkedHashSet<>(schoolDetail.getShiftDetailsList())));
        }
    }

    private void pruneFees(SchoolDetail schoolDetail) {
        if (!CollectionUtils.isEmpty(schoolDetail.getFeesDetails())) {
            schoolDetail.setFeesDetails(
                    Lists.newArrayList(new LinkedHashSet<>(schoolDetail.getFeesDetails())));
        }
    }
}
