package com.paytm.digital.education.ingestion.converter;


import static com.paytm.digital.education.utility.CommonUtils.booleanToString;
import static com.paytm.digital.education.utility.CommonUtils.stringToBoolean;

import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.ingestion.request.StreamDataRequest;
import com.paytm.digital.education.ingestion.sheets.StreamForm;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class StreamDataConverter {

    public static StreamDataRequest convertToStreamRequest(final StreamForm form) {
        if (Objects.nonNull(form)) {
            return StreamDataRequest.builder()
                    .streamId(form.getStreamId())
                    .name(form.getStreamName().trim())
                    .shortName(form.getShortName().trim())
                    .logo(form.getLogo().trim())
                    .priority(form.getGlobalPriority())
                    .isEnabled(stringToBoolean(
                            form.getStatusActive()))
                    .build();
        }
        return null;
    }

    public static List<StreamForm> convertToFormData(final List<StreamEntity> entityList) {
        if (!CollectionUtils.isEmpty(entityList)) {
            return entityList.stream()
                    .map(entity -> StreamForm.builder()
                            .streamId(entity.getStreamId())
                            .streamName(entity.getName())
                            .shortName(entity.getShortName())
                            .logo(entity.getLogo())
                            .globalPriority(entity.getPriority())
                            .statusActive(booleanToString(
                                    entity.getIsEnabled()))
                            .build())
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

}
