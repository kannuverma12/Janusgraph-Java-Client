package com.paytm.digital.education.coaching.ingestion.transformer.importdata;

import com.paytm.digital.education.coaching.ingestion.model.googleform.StreamForm;
import com.paytm.digital.education.coaching.producer.model.request.StreamDataRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImportStreamTransformer {

    public static StreamDataRequest convert(final StreamForm form) {
        if (null == form) {
            return null;
        }
        return StreamDataRequest.builder()
                .streamId(form.getStreamId())
                .name(form.getStreamName())
                .logo(form.getLogo())
                .priority(form.getGlobalPriority())
                .isEnabled(ImportCommonTransformer.convertStringToBoolean(
                        form.getStatusActive()))
                .build();
    }
}
