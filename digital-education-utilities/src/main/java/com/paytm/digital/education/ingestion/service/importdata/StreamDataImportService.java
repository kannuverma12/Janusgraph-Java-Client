package com.paytm.digital.education.ingestion.service.importdata;

import static com.paytm.digital.education.ingestion.constant.IngestionConstants.EMPTY_STRING;
import static com.paytm.digital.education.ingestion.constant.IngestionConstants.STREAM_COMPONENT;
import static com.paytm.digital.education.ingestion.constant.IngestionConstants.STREAM_SHEET_ID;
import static com.paytm.digital.education.mapping.ErrorEnum.BLANK_PAYTM_STREAM_NAME;

import com.paytm.digital.education.database.entity.StreamEntity;
import com.paytm.digital.education.exception.BadRequestException;
import com.paytm.digital.education.ingestion.converter.StreamDataConverter;
import com.paytm.digital.education.ingestion.request.DataImportPropertiesRequest;
import com.paytm.digital.education.ingestion.request.StreamDataRequest;
import com.paytm.digital.education.ingestion.response.DataImportPropertiesResponse;
import com.paytm.digital.education.ingestion.response.ImportResponse;
import com.paytm.digital.education.ingestion.service.StreamIngestorService;
import com.paytm.digital.education.ingestion.sheets.StreamForm;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StreamDataImportService extends AbstractImportService implements ImportService {

    private static Logger log = LoggerFactory.getLogger(StreamDataImportService.class);

    private static final String TYPE = "Stream";

    @Value("${explore.stream.logo.prefix}")
    private String streamLogoPrefix;

    @Autowired private StreamIngestorService streamManagerService;

    @Override
    public ImportResponse ingest() {

        final DataImportPropertiesResponse dataImportPropertiesResponse = this.getProperties(
                StreamForm.class,
                DataImportPropertiesRequest.builder()
                        .sheetIdKey(STREAM_SHEET_ID)
                        .build());

        final List<Object> streamFormData = this.getFormData(dataImportPropertiesResponse);

        final List<StreamForm> failedStreamFormList = this.getFailedData(
                TYPE, StreamForm.class, STREAM_COMPONENT);

        return this.processRecords(streamFormData,
                failedStreamFormList, StreamForm.class, TYPE);
    }

    @Override
    protected <T> void upsertFailedRecords(T form, Class<T> clazz) {
        final StreamForm newStreamForm = (StreamForm) clazz.cast(form);
        try {
            validateStreamData(newStreamForm);
            final StreamDataRequest request = this.buildRequest(newStreamForm);
            if (null == newStreamForm.getStreamId()) {
                this.streamManagerService.createStream(request);
            } else {
                this.streamManagerService.updateStream(request);
            }
        } catch (final Exception e) {
            log.error("Got Exception in upsertFailedRecords for input: {}, exception: ", form, e);
        }
    }

    @Override
    protected <T> void upsertNewRecords(final T form, final List<Object> failedDataList,
            final Class<T> clazz) {
        final StreamForm newStreamForm = (StreamForm) clazz.cast(form);
        StreamEntity response = null;
        String failureMessage = EMPTY_STRING;
        try {
            validateStreamData(newStreamForm);
            final StreamDataRequest request = this.buildRequest(newStreamForm);
            if (null == newStreamForm.getStreamId()) {
                response = this.streamManagerService.createStream(request);
            } else {
                response = this.streamManagerService.updateStream(request);
            }
        } catch (final Exception e) {
            log.error("Got Exception in upsertNewRecords for input: {}, exception: ", form, e);
            failureMessage = e.getMessage();
        }

        if (null == response || null == response.getStreamId()) {
            log.error("Error Response for stream : {} in create/update : {}", newStreamForm,
                    response);
            if (EMPTY_STRING.equals(failureMessage)) {
                failureMessage = "Failed to put data in Stream collection";
            }
            this.addToFailedList(form,
                    failureMessage, true, STREAM_COMPONENT, TYPE, failedDataList);
        }
    }

    private StreamDataRequest buildRequest(final StreamForm form) {
        final StreamDataRequest request = StreamDataConverter.convertToStreamRequest(form);
        request.setLogo(this.uploadFile(request.getLogo(), this.streamLogoPrefix));
        return request;
    }

    private void validateStreamData(StreamForm streamForm) {
        if (StringUtils.isBlank(streamForm.getStreamName())) {
            throw new BadRequestException(BLANK_PAYTM_STREAM_NAME,
                    BLANK_PAYTM_STREAM_NAME.getExternalMessage());
        }
    }
}
