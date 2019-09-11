package com.paytm.digital.education.coaching.ingestion.service.impl;

import com.paytm.digital.education.coaching.exeptions.CoachingBaseException;
import com.paytm.digital.education.coaching.http.HttpConstants;
import com.paytm.digital.education.coaching.http.HttpRequestDetails;
import com.paytm.digital.education.coaching.ingestion.model.IngestorResponse;
import com.paytm.digital.education.coaching.ingestion.model.googleform.StreamForm;
import com.paytm.digital.education.coaching.ingestion.model.properties.PropertiesRequest;
import com.paytm.digital.education.coaching.ingestion.model.properties.PropertiesResponse;
import com.paytm.digital.education.coaching.ingestion.service.IngestorService;
import com.paytm.digital.education.coaching.ingestion.service.IngestorServiceHelper;
import com.paytm.digital.education.coaching.ingestion.transformer.IngestorStreamTransformer;
import com.paytm.digital.education.coaching.producer.model.dto.StreamDTO;
import com.paytm.digital.education.coaching.producer.model.request.StreamDataRequest;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EMPTY_STRING;
import static com.paytm.digital.education.coaching.constants.GoogleSheetConstants.STREAM_SHEET_HEADER_RANGE;
import static com.paytm.digital.education.coaching.constants.GoogleSheetConstants.STREAM_SHEET_ID;
import static com.paytm.digital.education.coaching.constants.GoogleSheetConstants.STREAM_SHEET_RANGE_TEMPLATE;
import static com.paytm.digital.education.coaching.constants.GoogleSheetConstants.STREAM_SHEET_START_ROW;

@Slf4j
@Service
public class StreamIngestorService extends IngestorServiceHelper implements IngestorService {

    private static final String TYPE = "Stream";

    @Value("${coaching.producer.streamApi.url}")
    private String producerStreamApiUrl;

    @Override
    public IngestorResponse ingest() {

        final PropertiesResponse propertiesResponse = this.getProperties(PropertiesRequest.builder()
                .sheetIdKey(STREAM_SHEET_ID)
                .sheetHeaderRangeKey(STREAM_SHEET_HEADER_RANGE)
                .sheetStartRowKey(STREAM_SHEET_START_ROW)
                .sheetRangeTemplateKey(STREAM_SHEET_RANGE_TEMPLATE)
                .build());

        final List<Object> streamFormData = this.getFormData(propertiesResponse);

        final List<StreamForm> failedStreamFormList = this.getFailedData(
                TYPE, StreamForm.class, COACHING);

        return this.processRecords(streamFormData,
                failedStreamFormList, StreamForm.class, TYPE);
    }

    @Override
    protected <T> void upsertFailedRecords(T form, Class<T> clazz) {
        final StreamForm newStreamForm = (StreamForm) clazz.cast(form);
        try {
            if (null == newStreamForm.getStreamId()) {
                this.makeCall(IngestorStreamTransformer.convert(newStreamForm),
                        HttpMethod.POST);
            } else {
                this.makeCall(IngestorStreamTransformer.convert(newStreamForm),
                        HttpMethod.PUT);
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
            if (null == newStreamForm.getStreamId()) {
                response = this.makeCall(IngestorStreamTransformer.convert(newStreamForm),
                        HttpMethod.POST);
            } else {
                response = this.makeCall(IngestorStreamTransformer.convert(newStreamForm),
                        HttpMethod.PUT);
            }
        } catch (final Exception e) {
            log.error("Got Exception in upsertNewRecords for input: {}, exception: ", form, e);
            failureMessage = e.getMessage();
        }

        if (null == response || !response.getStatusCode().is2xxSuccessful()
                || null == response.getBody() || null == response.getBody().getStreamId()) {

            if (EMPTY_STRING.equals(failureMessage)) {
                failureMessage = "Failed to put data in Stream collection";
            }

            this.addToFailedList(JsonUtils.toJson(form),
                    failureMessage, true, COACHING, TYPE, failedDataList);
        }
    }

    private ResponseEntity<StreamDTO> makeCall(final StreamDataRequest request,
            final HttpMethod method) throws CoachingBaseException {

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        final HttpRequestDetails requestDetails = HttpRequestDetails.builder()
                .requestMethod(method)
                .url(this.producerStreamApiUrl)
                .body(request)
                .headers(httpHeaders)
                .requestApiName("StreamIngestorService")
                .build();

        return this.httpUtil.exchange(this.restTemplateFactory.getRestTemplate(
                HttpConstants.GENERIC_HTTP_SERVICE), requestDetails, StreamDTO.class);
    }
}
