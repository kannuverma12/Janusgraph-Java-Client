package com.paytm.digital.education.coaching.consumer.transformer;

import com.paytm.digital.education.coaching.consumer.model.dto.LandingPageStreamDto;
import com.paytm.digital.education.coaching.utils.ImageUtils;
import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.utility.CommonUtil;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.STREAM_PLACEHOLDER;
import static com.paytm.digital.education.constant.CommonConstants.STREAMS;

@Component
public class LandingPageStreamTransformer {

    private static final Logger log = LoggerFactory.getLogger(LandingPageStreamTransformer.class);

    public List<LandingPageStreamDto> getLandingPageStreamDtoFromStreamEntity(
            List<StreamEntity> streamEntities) {
        List<LandingPageStreamDto> landingPageStreamDtoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(streamEntities)) {
            for (StreamEntity streamEntity : streamEntities) {
                LandingPageStreamDto landingPageStreamDto = LandingPageStreamDto.builder()
                        .entityId(streamEntity.getStreamId())
                        .key(streamEntity.getName())
                        .fullName(streamEntity.getName())
                        .urlDisplayKey(CommonUtil.convertNameToUrlDisplayName(
                                streamEntity.getName()))
                        .displayName(streamEntity.getShortName())
                        .logo(ImageUtils.getImageWithAbsolutePath(streamEntity.getLogo(),
                                STREAM_PLACEHOLDER, STREAMS))
                        .build();
                landingPageStreamDtoList.add(landingPageStreamDto);
            }
        }
        return landingPageStreamDtoList;
    }
}
