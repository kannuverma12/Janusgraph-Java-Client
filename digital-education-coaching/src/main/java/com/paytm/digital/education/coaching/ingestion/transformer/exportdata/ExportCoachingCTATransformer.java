package com.paytm.digital.education.coaching.ingestion.transformer.exportdata;

import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingCTAForm;
import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingCourseFeatureForm;
import com.paytm.digital.education.database.entity.CoachingCourseFeatureEntity;
import com.paytm.digital.education.database.entity.CoachingCtaEntity;
import com.paytm.digital.education.utility.CommonUtil;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.paytm.digital.education.constant.CommonConstants.COACHING_COURSE_FEATURE;

public class ExportCoachingCTATransformer {

    public static List<CoachingCTAForm> convert(
            final List<CoachingCtaEntity> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return new ArrayList<>();
        }
        return entityList.stream()
                .map(entity -> CoachingCTAForm.builder()
                        .ctaId(entity.getCtaId())
                        .name(entity.getName())
                        .description(entity.getDescription())
                        .logoUrl(entity.getLogoUrl())
                        .ctaUrl(entity.getUrl())
                        .ctaType(entity.getCtaType())
                        .properties(ExportCommonTransformer.convertListToString(entity.getProperties()))
                        .build())
                .collect(Collectors.toList());
    }
}
