package com.paytm.digital.education.coaching.ingestion.transformer.importdata;

import com.paytm.digital.education.coaching.enums.CoachingCourseFeatureName;
import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingCourseFeatureForm;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCourseFeatureDataRequest;

public class ImportCoachingCourseFeatureTransformer {

    public static CoachingCourseFeatureDataRequest convert(final CoachingCourseFeatureForm form) {
        if (null == form) {
            return null;
        }
        return CoachingCourseFeatureDataRequest.builder()
                .coachingCourseFeatureId(form.getCourseFacilityId())
                .instituteId(form.getInstituteId())
                .coachingCourseFeatureName(
                        CoachingCourseFeatureName.fromString(form.getFacilityType()))
                .logo(form.getLogo())
                .description(form.getFacilityDescription())
                .priority(form.getPriority())
                .isEnabled(ImportCommonTransformer.convertStringToBoolean(
                        form.getStatusActive()))
                .build();
    }
}
