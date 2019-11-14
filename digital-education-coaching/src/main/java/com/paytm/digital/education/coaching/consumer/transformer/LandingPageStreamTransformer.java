package com.paytm.digital.education.coaching.consumer.transformer;

import com.paytm.digital.education.coaching.consumer.model.dto.LandingPageStreamDto;
import com.paytm.digital.education.coaching.utils.ImageUtils;
import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.utility.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.STREAM_PLACEHOLDER;
import static com.paytm.digital.education.constant.CommonConstants.STREAMS;

@Slf4j
@Component
public class LandingPageStreamTransformer {

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
