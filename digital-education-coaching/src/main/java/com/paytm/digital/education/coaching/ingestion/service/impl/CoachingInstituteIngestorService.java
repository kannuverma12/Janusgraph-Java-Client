package com.paytm.digital.education.coaching.ingestion.service.impl;

import com.paytm.digital.education.coaching.exeptions.CoachingBaseException;
import com.paytm.digital.education.coaching.http.HttpConstants;
import com.paytm.digital.education.coaching.http.HttpRequestDetails;
import com.paytm.digital.education.coaching.ingestion.model.IngestorResponse;
import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingInstituteForm;
import com.paytm.digital.education.coaching.ingestion.model.properties.PropertiesRequest;
import com.paytm.digital.education.coaching.ingestion.model.properties.PropertiesResponse;
import com.paytm.digital.education.coaching.ingestion.service.IngestorService;
import com.paytm.digital.education.coaching.ingestion.service.IngestorServiceHelper;
import com.paytm.digital.education.coaching.ingestion.transformer.IngestorCoachingInstituteTransformer;
import com.paytm.digital.education.coaching.producer.model.dto.CoachingInstituteDTO;
import com.paytm.digital.education.coaching.producer.model.request.CoachingInstituteDataRequest;
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
import static com.paytm.digital.education.coaching.constants.GoogleSheetConstants.COACHING_INSTITUTE_SHEET_HEADER_RANGE;
import static com.paytm.digital.education.coaching.constants.GoogleSheetConstants.COACHING_INSTITUTE_SHEET_ID;
import static com.paytm.digital.education.coaching.constants.GoogleSheetConstants.COACHING_INSTITUTE_SHEET_RANGE_TEMPLATE;
import static com.paytm.digital.education.coaching.constants.GoogleSheetConstants.COACHING_INSTITUTE_SHEET_START_ROW;

@Slf4j
@Service
public class CoachingInstituteIngestorService extends IngestorServiceHelper
        implements IngestorService {

    private static final String TYPE = "CoachingInstitute";

    @Value("${coaching.producer.instituteApi.url}")
    private String producerInstituteApiUrl;

    @Override
    public IngestorResponse ingest() {

        final PropertiesResponse propertiesResponse = this.getProperties(PropertiesRequest.builder()
                .sheetIdKey(COACHING_INSTITUTE_SHEET_ID)
                .sheetHeaderRangeKey(COACHING_INSTITUTE_SHEET_HEADER_RANGE)
                .sheetStartRowKey(COACHING_INSTITUTE_SHEET_START_ROW)
                .sheetRangeTemplateKey(COACHING_INSTITUTE_SHEET_RANGE_TEMPLATE)
                .build());

        final List<Object> instituteFormData = this.getFormData(propertiesResponse);

        final List<CoachingInstituteForm> failedInstituteFormList = this
                .getFailedData(TYPE, CoachingInstituteForm.class, COACHING);

        return this.processRecords(instituteFormData,
                failedInstituteFormList, CoachingInstituteForm.class, TYPE
        );
    }

    @Override
    protected <T> void upsertNewRecords(final T form, final List<Object> failedDataList,
            final Class<T> clazz) {
        final CoachingInstituteForm instituteForm = (CoachingInstituteForm) clazz.cast(form);

        ResponseEntity<CoachingInstituteDTO> response = null;
        String failureMessage = EMPTY_STRING;
        try {
            if (null == instituteForm.getInstituteId()) {
                response = this.makeCall(IngestorCoachingInstituteTransformer.convert(
                        instituteForm), HttpMethod.POST);
            } else {
                response = this.makeCall(IngestorCoachingInstituteTransformer.convert(
                        instituteForm), HttpMethod.PUT);
            }
        } catch (final Exception e) {
            log.error("Got Exception in upsertNewRecords for input: {}, exception: ", form, e);
            failureMessage = e.getMessage();
        }

        if (null == response || !response.getStatusCode().is2xxSuccessful()
                || null == response.getBody() || null == response.getBody().getInstituteId()) {

            if (EMPTY_STRING.equals(failureMessage)) {
                failureMessage = "Failed to put new data in CoachingInstitute collection";
            }

            this.addToFailedList(JsonUtils.toJson(form),
                    failureMessage, true, COACHING, TYPE, failedDataList);
        }
    }

    @Override
    protected <T> void upsertFailedRecords(T form, Class<T> clazz) {
        final CoachingInstituteForm instituteForm = (CoachingInstituteForm) clazz.cast(form);
        try {
            if (null == instituteForm.getInstituteId()) {
                this.makeCall(IngestorCoachingInstituteTransformer.convert(
                        instituteForm), HttpMethod.POST);
            } else {
                this.makeCall(IngestorCoachingInstituteTransformer.convert(
                        instituteForm), HttpMethod.PUT);
            }
        } catch (final Exception e) {
            log.error("Got Exception in upsertFailedRecords for input: {}, exception: ", form, e);
        }
    }

    private ResponseEntity<CoachingInstituteDTO> makeCall(
            final CoachingInstituteDataRequest request,
            final HttpMethod method) throws CoachingBaseException {

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        final HttpRequestDetails requestDetails = HttpRequestDetails.builder()
                .requestMethod(method)
                .url(this.producerInstituteApiUrl)
                .body(request)
                .headers(httpHeaders)
                .requestApiName("CoachingInstituteIngestorService")
                .build();

        return this.httpUtil.exchange(this.restTemplateFactory.getRestTemplate(
                HttpConstants.GENERIC_HTTP_SERVICE), requestDetails, CoachingInstituteDTO.class);
    }
}
