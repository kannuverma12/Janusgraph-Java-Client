package com.paytm.digital.education.coaching.ingestion.transformer.exportdata;

import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingBannerForm;
import com.paytm.digital.education.database.entity.CoachingBannerEntity;
import com.paytm.digital.education.utility.CommonUtil;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.paytm.digital.education.constant.CommonConstants.COACHING_BANNER;

public class ExportCoachingBannerTransformer {

    public static List<CoachingBannerForm> convert(final List<CoachingBannerEntity> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return new ArrayList<>();
        }
        return entityList.stream()
                .map(entity -> CoachingBannerForm.builder()
                        .id(entity.getCoachingBannerId())
                        .bannerImageUrl(CommonUtil.getAbsoluteUrl(entity.getBannerImageUrl(),
                                COACHING_BANNER))
                        .redirectionUrl(entity.getRedirectionUrl())
                        .globalPriority(entity.getPriority())
                        .statusActive(ExportCommonTransformer.convertBooleanToString(
                                entity.getIsEnabled()))
                        .build())
                .collect(Collectors.toList());
    }
}
