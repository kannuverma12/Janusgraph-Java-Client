package com.paytm.digital.education.coaching.ingestion.service.importdata.impl;

import com.paytm.digital.education.coaching.exeptions.CoachingBaseException;
import com.paytm.digital.education.coaching.ingestion.model.ImportResponse;
import com.paytm.digital.education.coaching.ingestion.model.googleform.StreamForm;
import com.paytm.digital.education.coaching.ingestion.model.properties.DataImportPropertiesRequest;
import com.paytm.digital.education.coaching.ingestion.model.properties.DataImportPropertiesResponse;
import com.paytm.digital.education.coaching.ingestion.service.importdata.AbstractImportService;
import com.paytm.digital.education.coaching.ingestion.service.importdata.ImportService;
import com.paytm.digital.education.coaching.ingestion.transformer.importdata.ImportStreamTransformer;
import com.paytm.digital.education.coaching.producer.controller.ProducerStreamController;
import com.paytm.digital.education.coaching.producer.model.dto.StreamDTO;
import com.paytm.digital.education.coaching.producer.model.request.StreamDataRequest;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EMPTY_STRING;
import static com.paytm.digital.education.coaching.constants.GoogleSheetIngestionConstants.STREAM_SHEET_HEADER_RANGE;
import static com.paytm.digital.education.coaching.constants.GoogleSheetIngestionConstants.STREAM_SHEET_ID;
import static com.paytm.digital.education.coaching.constants.GoogleSheetIngestionConstants.STREAM_SHEET_RANGE_TEMPLATE;
import static com.paytm.digital.education.coaching.constants.GoogleSheetIngestionConstants.STREAM_SHEET_START_ROW;

@Slf4j
@Service
public class StreamImportService extends AbstractImportService implements ImportService {

    private static final String TYPE = "Stream";

    @Value("${coaching.stream.logo.prefix}")
    private String coachingStreamLogoPrefix;

    @Autowired
    private ProducerStreamController producerStreamController;

    @Override
    public ImportResponse ingest() {

        final DataImportPropertiesResponse dataImportPropertiesResponse = this.getProperties(
                DataImportPropertiesRequest.builder()
                        .sheetIdKey(STREAM_SHEET_ID)
                        .sheetHeaderRangeKey(STREAM_SHEET_HEADER_RANGE)
                        .sheetStartRowKey(STREAM_SHEET_START_ROW)
                        .sheetRangeTemplateKey(STREAM_SHEET_RANGE_TEMPLATE)
                        .build());

        final List<Object> streamFormData = this.getFormData(dataImportPropertiesResponse);

        final List<StreamForm> failedStreamFormList = this.getFailedData(
                TYPE, StreamForm.class, COACHING);

        return this.processRecords(streamFormData,
                failedStreamFormList, StreamForm.class, TYPE);
    }

    @Override
    protected <T> void upsertFailedRecords(T form, Class<T> clazz) {
        final StreamForm newStreamForm = (StreamForm) clazz.cast(form);
        try {
            final StreamDataRequest request = this.buildRequest(newStreamForm);
            if (null == newStreamForm.getStreamId()) {
                this.producerStreamController.createStream(request);
            } else {
                this.producerStreamController.updateStream(request);
            }
        } catch (final Exception e) {
            log.error("Got Exception in upsertFailedRecords for input: {}, exception: ", form, e);
        }
    }

    @Override
    protected <T> void upsertNewRecords(final T form, final List<Object> failedDataList,
            final Class<T> clazz) {
        final StreamForm newStreamForm = (StreamForm) clazz.cast(form);
        ResponseEntity<StreamDTO> response = null;
        String failureMessage = EMPTY_STRING;
        try {
            final StreamDataRequest request = this.buildRequest(newStreamForm);
            if (null == newStreamForm.getStreamId()) {
                response = this.producerStreamController.createStream(request);
            } else {
                response = this.producerStreamController.updateStream(request);
            }
        } catch (final Exception e) {
            log.error("Got Exception in upsertNewRecords for input: {}, exception: ", form, e);
            failureMessage = e.getMessage();
        }

        if (null == response || !response.getStatusCode().is2xxSuccessful()
                || null == response.getBody() || null == response.getBody().getStreamId()) {
            log.error("Response: {}", response);
            if (EMPTY_STRING.equals(failureMessage)) {
                failureMessage = "Failed to put data in Stream collection";
            }

            this.addToFailedList(JsonUtils.toJson(form),
                    failureMessage, true, COACHING, TYPE, failedDataList);
        }
    }

    private StreamDataRequest buildRequest(final StreamForm form) throws CoachingBaseException {
        final StreamDataRequest request = ImportStreamTransformer.convert(form);
        request.setLogo(this.uploadFile(request.getLogo(), this.coachingStreamLogoPrefix));
        return request;
    }
}
