package com.paytm.digital.education.coaching.ingestion.transformer.importdata;

import com.paytm.digital.education.coaching.enums.CoachingCourseFeatureName;
import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingCourseFeatureForm;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCourseFeatureDataRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImportCoachingCourseFeatureTransformer {

    public static CoachingCourseFeatureDataRequest convert(final CoachingCourseFeatureForm form) {
        if (null == form) {
            return null;
        }
        final CoachingCourseFeatureDataRequest request = CoachingCourseFeatureDataRequest.builder()
                .coachingCourseFeatureId(form.getCourseFacilityId())
                .instituteId(form.getInstituteId())
                .coachingCourseFeatureName(
                        CoachingCourseFeatureName.fromString(form.getFacilityType()))
                .logo(form.getLogo())
                .description(form.getFacilityDescription())
                .priority(form.getPriority())
                .coachingCourseFeatureName(CoachingCourseFeatureName.fromString(
                        form.getFacilityType()))
                .isEnabled(ImportCommonTransformer.convertStringToBoolean(
                        form.getStatusActive()))
                .build();

        log.info("CoachingCenterDataRequest: {}", request);
        return request;
    }
}
