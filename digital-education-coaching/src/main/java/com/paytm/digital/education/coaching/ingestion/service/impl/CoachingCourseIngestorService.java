package com.paytm.digital.education.coaching.ingestion.service.impl;

import com.paytm.digital.education.coaching.exeptions.CoachingBaseException;
import com.paytm.digital.education.coaching.http.HttpConstants;
import com.paytm.digital.education.coaching.http.HttpRequestDetails;
import com.paytm.digital.education.coaching.ingestion.model.IngestorResponse;
import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingCourseForm;
import com.paytm.digital.education.coaching.ingestion.model.properties.PropertiesRequest;
import com.paytm.digital.education.coaching.ingestion.model.properties.PropertiesResponse;
import com.paytm.digital.education.coaching.ingestion.service.IngestorService;
import com.paytm.digital.education.coaching.ingestion.service.IngestorServiceHelper;
import com.paytm.digital.education.coaching.ingestion.transformer.IngestorCoachingCourseTransformer;
import com.paytm.digital.education.coaching.producer.model.dto.CoachingCourseDTO;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCourseDataRequest;
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
import static com.paytm.digital.education.coaching.constants.GoogleSheetConstants.COACHING_COURSE_SHEET_HEADER_RANGE;
import static com.paytm.digital.education.coaching.constants.GoogleSheetConstants.COACHING_COURSE_SHEET_ID;
import static com.paytm.digital.education.coaching.constants.GoogleSheetConstants.COACHING_COURSE_SHEET_RANGE_TEMPLATE;
import static com.paytm.digital.education.coaching.constants.GoogleSheetConstants.COACHING_COURSE_SHEET_START_ROW;

@Slf4j
@Service
public class CoachingCourseIngestorService extends IngestorServiceHelper
        implements IngestorService {

    private static final String TYPE = "CoachingCourse";

    @Value("${coaching.producer.courseApi.url}")
    private String producerCourseApiUrl;

    @Override
    public IngestorResponse ingest() {

        final PropertiesResponse propertiesResponse = this.getProperties(PropertiesRequest.builder()
                .sheetIdKey(COACHING_COURSE_SHEET_ID)
                .sheetHeaderRangeKey(COACHING_COURSE_SHEET_HEADER_RANGE)
                .sheetStartRowKey(COACHING_COURSE_SHEET_START_ROW)
                .sheetRangeTemplateKey(COACHING_COURSE_SHEET_RANGE_TEMPLATE)
                .build());

        final List<Object> courseFormData = this.getFormData(propertiesResponse);

        final List<CoachingCourseForm> failedCourseFormList = this
                .getFailedData(TYPE, CoachingCourseForm.class, COACHING);

        return this.processRecords(courseFormData,
                failedCourseFormList, CoachingCourseForm.class, TYPE
        );
    }

    @Override
    protected <T> void upsertNewRecords(final T form, final List<Object> failedDataList,
            final Class<T> clazz) {

        final CoachingCourseForm courseForm = (CoachingCourseForm) clazz.cast(form);

        ResponseEntity<CoachingCourseDTO> response = null;
        String failureMessage = EMPTY_STRING;
        try {
            if (null == courseForm.getCourseId()) {
                response = this.makeCall(IngestorCoachingCourseTransformer.convert(courseForm),
                        HttpMethod.POST);
            } else {
                response = this.makeCall(IngestorCoachingCourseTransformer.convert(courseForm),
                        HttpMethod.PUT);
            }
        } catch (final Exception e) {
            log.error("Got Exception in upsertNewRecords for input: {}, exception: ", form, e);
            failureMessage = e.getMessage();
        }

        if (null == response || !response.getStatusCode().is2xxSuccessful()
                || null == response.getBody() || null == response.getBody().getCourseId()) {

            if (EMPTY_STRING.equals(failureMessage)) {
                failureMessage = "Failed to put new data in CoachingCourse collection";
            }

            this.addToFailedList(JsonUtils.toJson(form),
                    failureMessage, true, COACHING, TYPE, failedDataList);
        }
    }

    @Override
    protected <T> void upsertFailedRecords(T form, Class<T> clazz) {
        final CoachingCourseForm courseForm = (CoachingCourseForm) clazz.cast(form);
        try {
            if (null == courseForm.getCourseId()) {
                this.makeCall(IngestorCoachingCourseTransformer.convert(courseForm),
                        HttpMethod.POST);
            } else {
                this.makeCall(IngestorCoachingCourseTransformer.convert(courseForm),
                        HttpMethod.PUT);
            }
        } catch (final Exception e) {
            log.error("Got Exception in upsertFailedRecords for input: {}, exception: ", form, e);
        }
    }

    private ResponseEntity<CoachingCourseDTO> makeCall(final CoachingCourseDataRequest request,
            final HttpMethod method) throws CoachingBaseException {

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        final HttpRequestDetails requestDetails = HttpRequestDetails.builder()
                .requestMethod(method)
                .url(this.producerCourseApiUrl)
                .body(request)
                .headers(httpHeaders)
                .requestApiName("CoachingCourseIngestorService")
                .build();

        return this.httpUtil.exchange(this.restTemplateFactory.getRestTemplate(
                HttpConstants.GENERIC_HTTP_SERVICE), requestDetails, CoachingCourseDTO.class);
    }
}
