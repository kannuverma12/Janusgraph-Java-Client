package com.paytm.digital.education.coaching.ingestion.service.impl;

import com.paytm.digital.education.coaching.exeptions.CoachingBaseException;
import com.paytm.digital.education.coaching.http.HttpConstants;
import com.paytm.digital.education.coaching.http.HttpRequestDetails;
import com.paytm.digital.education.coaching.ingestion.model.IngestorResponse;
import com.paytm.digital.education.coaching.ingestion.model.googleform.CompetitiveExamForm;
import com.paytm.digital.education.coaching.ingestion.model.properties.PropertiesRequest;
import com.paytm.digital.education.coaching.ingestion.model.properties.PropertiesResponse;
import com.paytm.digital.education.coaching.ingestion.service.IngestorService;
import com.paytm.digital.education.coaching.ingestion.service.IngestorServiceHelper;
import com.paytm.digital.education.coaching.ingestion.transformer.IngestorCompetitiveExamTransformer;
import com.paytm.digital.education.coaching.producer.model.dto.TargetExamDTO;
import com.paytm.digital.education.coaching.producer.model.request.TargetExamUpdateRequest;
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
import static com.paytm.digital.education.coaching.constants.GoogleSheetConstants.COMPETITIVE_EXAM_SHEET_HEADER_RANGE;
import static com.paytm.digital.education.coaching.constants.GoogleSheetConstants.COMPETITIVE_EXAM_SHEET_ID;
import static com.paytm.digital.education.coaching.constants.GoogleSheetConstants.COMPETITIVE_EXAM_SHEET_RANGE_TEMPLATE;
import static com.paytm.digital.education.coaching.constants.GoogleSheetConstants.COMPETITIVE_EXAM_SHEET_START_ROW;

@Slf4j
@Service
public class CompetitiveExamIngestorService extends IngestorServiceHelper
        implements IngestorService {

    private static final String TYPE = "CompetitiveExam";

    @Value("${coaching.producer.competitiveExamApi.url}")
    private String producerCompetitiveExamApiUrl;

    @Override
    public IngestorResponse ingest() {

        final PropertiesResponse propertiesResponse = this.getProperties(PropertiesRequest.builder()
                .sheetIdKey(COMPETITIVE_EXAM_SHEET_ID)
                .sheetHeaderRangeKey(COMPETITIVE_EXAM_SHEET_HEADER_RANGE)
                .sheetStartRowKey(COMPETITIVE_EXAM_SHEET_START_ROW)
                .sheetRangeTemplateKey(COMPETITIVE_EXAM_SHEET_RANGE_TEMPLATE)
                .build());

        final List<Object> competitiveExamFormData = this.getFormData(propertiesResponse);

        final List<CompetitiveExamForm> failedCompetitiveExamFormList = this.getFailedData(
                TYPE, CompetitiveExamForm.class, COACHING);

        return this.processRecords(competitiveExamFormData,
                failedCompetitiveExamFormList, CompetitiveExamForm.class, TYPE);
    }

    @Override
    protected <T> void upsertFailedRecords(T form, Class<T> clazz) {
        final CompetitiveExamForm newCompetitiveExamForm = (CompetitiveExamForm) clazz.cast(form);
        try {
            if (null != newCompetitiveExamForm.getExamId()) {
                this.makeCall(IngestorCompetitiveExamTransformer.convert(newCompetitiveExamForm),
                        HttpMethod.PATCH);
            }
        } catch (final Exception e) {
            log.error("Got Exception in upsertFailedRecords for input: {}, exception: ", form, e);
        }
    }

    @Override
    protected <T> void upsertNewRecords(final T form, final List<Object> failedDataList,
            final Class<T> clazz) {
        final CompetitiveExamForm newCompetitiveExamForm = (CompetitiveExamForm) clazz.cast(form);
        ResponseEntity<TargetExamDTO> response = null;
        String failureMessage = EMPTY_STRING;
        try {
            if (null != newCompetitiveExamForm.getExamId()) {
                response = this.makeCall(IngestorCompetitiveExamTransformer.convert(
                        newCompetitiveExamForm), HttpMethod.PATCH);
            }
        } catch (final Exception e) {
            log.error("Got Exception in upsertNewRecords for input: {}, exception: ", form, e);
            failureMessage = e.getMessage();
        }

        if (null == response || !response.getStatusCode().is2xxSuccessful()
                || null == response.getBody() || null == response.getBody().getExamId()) {

            if (EMPTY_STRING.equals(failureMessage)) {
                failureMessage = "Failed to put data in CompetitiveExam collection";
            }

            this.addToFailedList(JsonUtils.toJson(form),
                    failureMessage, true, COACHING, TYPE, failedDataList);
        }
    }

    private ResponseEntity<TargetExamDTO> makeCall(final TargetExamUpdateRequest request,
            final HttpMethod method) throws CoachingBaseException {

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        final HttpRequestDetails requestDetails = HttpRequestDetails.builder()
                .requestMethod(method)
                .url(this.producerCompetitiveExamApiUrl)
                .body(request)
                .headers(httpHeaders)
                .requestApiName("CompetitiveExamIngestorService")
                .build();

        return this.httpUtil.exchange(this.restTemplateFactory.getRestTemplate(
                HttpConstants.GENERIC_HTTP_SERVICE), requestDetails, TargetExamDTO.class);
    }
}
