package com.paytm.digital.education.coaching.ingestion.transformer.exportdata;

import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingCourseFeatureForm;
import com.paytm.digital.education.database.entity.CoachingCourseFeatureEntity;
import com.paytm.digital.education.utility.CommonUtil;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.paytm.digital.education.constant.CommonConstants.COACHING_COURSE_FEATURE;

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
                        .logo(CommonUtil.getAbsoluteUrl(entity.getLogo(), COACHING_COURSE_FEATURE))
                        .facilityDescription(entity.getDescription())
                        .priority(entity.getPriority())
                        .statusActive(ExportCommonTransformer.convertBooleanToString(
                                entity.getIsEnabled()))
                        .build())
                .collect(Collectors.toList());
    }
}
