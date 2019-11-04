package com.paytm.digital.education.coaching.ingestion.transformer.exportdata;

import com.paytm.digital.education.coaching.ingestion.model.googleform.StreamForm;
import com.paytm.digital.education.database.entity.StreamEntity;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExportStreamTransformer {

    public static List<StreamForm> convert(final List<StreamEntity> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return new ArrayList<>();
        }
        return entityList.stream()
                .map(entity -> StreamForm.builder()
                        .streamId(entity.getStreamId())
                        .streamName(entity.getName())
                        .logo(entity.getLogo())
                        .globalPriority(entity.getPriority())
                        .statusActive(ExportCommonTransformer.convertBooleanToString(
                                entity.getIsEnabled()))
                        .build())
                .collect(Collectors.toList());
    }
}
