package com.paytm.digital.education.coaching.ingestion.transformer.exportdata;

import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingCourseFeatureForm;
import com.paytm.digital.education.database.entity.CoachingCourseFeatureEntity;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExportCoachingCourseFeatureTransformer {

    public static List<CoachingCourseFeatureForm> convert(
            final List<CoachingCourseFeatureEntity> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return new ArrayList<>();
        }
        return entityList.stream()
                .map(entity -> CoachingCourseFeatureForm.builder()
                        .courseFacilityId(entity.getCoachingCourseFeatureId())
                        .instituteId(entity.getInstituteId())
                        .facilityType(entity.getName())
                        .logo(entity.getLogo())
                        .facilityDescription(entity.getDescription())
                        .priority(entity.getPriority())
                        .statusActive(ExportCommonTransformer.convertBooleanToString(
                                entity.getIsEnabled()))
                        .build())
                .collect(Collectors.toList());
    }
}
