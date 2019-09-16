package com.paytm.digital.education.coaching.ingestion.service.impl;

import com.paytm.digital.education.coaching.ingestion.model.IngestorResponse;
import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingCourseForm;
import com.paytm.digital.education.coaching.ingestion.model.properties.PropertiesRequest;
import com.paytm.digital.education.coaching.ingestion.model.properties.PropertiesResponse;
import com.paytm.digital.education.coaching.ingestion.service.IngestorService;
import com.paytm.digital.education.coaching.ingestion.service.IngestorServiceHelper;
import com.paytm.digital.education.coaching.ingestion.transformer.IngestorCoachingCourseTransformer;
import com.paytm.digital.education.coaching.producer.controller.ProducerCoachingCourseController;
import com.paytm.digital.education.coaching.producer.model.dto.CoachingCourseDTO;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCourseDataRequest;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ProducerCoachingCourseController producerCoachingCourseController;

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

        return this.processRecords(courseFormData, failedCourseFormList,
                CoachingCourseForm.class, TYPE);
    }

    @Override
    protected <T> void upsertNewRecords(final T form, final List<Object> failedDataList,
            final Class<T> clazz) {

        final CoachingCourseForm courseForm = (CoachingCourseForm) clazz.cast(form);
        ResponseEntity<CoachingCourseDTO> response = null;
        String failureMessage = EMPTY_STRING;
        try {
            final CoachingCourseDataRequest request = IngestorCoachingCourseTransformer.convert(
                    courseForm);
            if (null == courseForm.getCourseId()) {
                response = this.producerCoachingCourseController.insertCoachingProgram(request);
            } else {
                response = this.producerCoachingCourseController.updateCoachingProgram(request);
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
            final CoachingCourseDataRequest request = IngestorCoachingCourseTransformer.convert(
                    courseForm);
            if (null == courseForm.getCourseId()) {
                this.producerCoachingCourseController.insertCoachingProgram(request);
            } else {
                this.producerCoachingCourseController.updateCoachingProgram(request);
            }
        } catch (final Exception e) {
            log.error("Got Exception in upsertFailedRecords for input: {}, exception: ", form, e);
        }
    }
}
