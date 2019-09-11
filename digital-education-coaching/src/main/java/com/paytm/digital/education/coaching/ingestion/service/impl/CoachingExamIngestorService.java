package com.paytm.digital.education.coaching.ingestion.service.impl;

import com.paytm.digital.education.coaching.exeptions.CoachingBaseException;
import com.paytm.digital.education.coaching.http.HttpConstants;
import com.paytm.digital.education.coaching.http.HttpRequestDetails;
import com.paytm.digital.education.coaching.ingestion.model.IngestorResponse;
import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingExamForm;
import com.paytm.digital.education.coaching.ingestion.model.properties.PropertiesRequest;
import com.paytm.digital.education.coaching.ingestion.model.properties.PropertiesResponse;
import com.paytm.digital.education.coaching.ingestion.service.IngestorService;
import com.paytm.digital.education.coaching.ingestion.service.IngestorServiceHelper;
import com.paytm.digital.education.coaching.ingestion.transformer.IngestorCoachingExamTransformer;
import com.paytm.digital.education.coaching.producer.model.dto.CoachingExamDTO;
import com.paytm.digital.education.coaching.producer.model.request.CoachingExamDataRequest;
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
import static com.paytm.digital.education.coaching.constants.GoogleSheetConstants.COACHING_EXAM_SHEET_HEADER_RANGE;
import static com.paytm.digital.education.coaching.constants.GoogleSheetConstants.COACHING_EXAM_SHEET_ID;
import static com.paytm.digital.education.coaching.constants.GoogleSheetConstants.COACHING_EXAM_SHEET_RANGE_TEMPLATE;
import static com.paytm.digital.education.coaching.constants.GoogleSheetConstants.COACHING_EXAM_SHEET_START_ROW;

@Slf4j
@Service
public class CoachingExamIngestorService extends IngestorServiceHelper implements IngestorService {

    private static final String TYPE = "CoachingExam";

    @Value("${coaching.producer.examApi.url}")
    private String producerExamApiUrl;

    @Override
    public IngestorResponse ingest() {

        final PropertiesResponse propertiesResponse = this.getProperties(PropertiesRequest.builder()
                .sheetIdKey(COACHING_EXAM_SHEET_ID)
                .sheetHeaderRangeKey(COACHING_EXAM_SHEET_HEADER_RANGE)
                .sheetStartRowKey(COACHING_EXAM_SHEET_START_ROW)
                .sheetRangeTemplateKey(COACHING_EXAM_SHEET_RANGE_TEMPLATE)
                .build());

        final List<Object> examFormData = this.getFormData(propertiesResponse);

        final List<CoachingExamForm> failedExamFormList = this
                .getFailedData(TYPE, CoachingExamForm.class, COACHING);

        return this.processRecords(examFormData,
                failedExamFormList, CoachingExamForm.class, TYPE);
    }

    @Override
    protected <T> void upsertNewRecords(final T form, final List<Object> failedDataList,
            final Class<T> clazz) {
        final CoachingExamForm examForm = (CoachingExamForm) clazz.cast(form);

        ResponseEntity<CoachingExamDTO> response = null;
        String failureMessage = EMPTY_STRING;
        try {
            if (null == examForm.getCoachingExamId()) {
                response = this.makeCall(IngestorCoachingExamTransformer.convert(examForm),
                        HttpMethod.POST);
            } else {
                response = this.makeCall(IngestorCoachingExamTransformer.convert(examForm),
                        HttpMethod.PUT);
            }
        } catch (final Exception e) {
            log.error("Got Exception in upsertNewRecords for input: {}, exception: ", form, e);
            failureMessage = e.getMessage();
        }

        if (null == response || !response.getStatusCode().is2xxSuccessful()
                || null == response.getBody() || null == response.getBody().getCoachingExamId()) {

            if (EMPTY_STRING.equals(failureMessage)) {
                failureMessage = "Failed to put new data in CoachingExam collection";
            }

            this.addToFailedList(JsonUtils.toJson(form),
                    failureMessage, true, COACHING, TYPE, failedDataList);
        }
    }

    @Override
    protected <T> void upsertFailedRecords(T form, Class<T> clazz) {
        final CoachingExamForm examForm = (CoachingExamForm) clazz.cast(form);
        try {
            if (null == examForm.getCoachingExamId()) {
                this.makeCall(IngestorCoachingExamTransformer.convert(examForm),
                        HttpMethod.POST);
            } else {
                this.makeCall(IngestorCoachingExamTransformer.convert(examForm),
                        HttpMethod.PUT);
            }
        } catch (final Exception e) {
            log.error("Got Exception in upsertFailedRecords for input: {}, exception: ", form, e);
        }
    }

    private ResponseEntity<CoachingExamDTO> makeCall(final CoachingExamDataRequest request,
            final HttpMethod method) throws CoachingBaseException {

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        final HttpRequestDetails requestDetails = HttpRequestDetails.builder()
                .requestMethod(method)
                .url(this.producerExamApiUrl)
                .body(request)
                .headers(httpHeaders)
                .requestApiName("CoachingExamIngestorService")
                .build();

        return this.httpUtil.exchange(this.restTemplateFactory.getRestTemplate(
                HttpConstants.GENERIC_HTTP_SERVICE), requestDetails, CoachingExamDTO.class);
    }
}
