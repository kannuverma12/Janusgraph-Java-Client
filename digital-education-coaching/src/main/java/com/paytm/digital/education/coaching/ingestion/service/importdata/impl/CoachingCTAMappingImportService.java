package com.paytm.digital.education.coaching.ingestion.service.importdata.impl;

import com.paytm.digital.education.coaching.ingestion.model.ImportResponse;
import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingCTAMappingForm;
import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingCourseForm;
import com.paytm.digital.education.coaching.ingestion.model.properties.DataImportPropertiesRequest;
import com.paytm.digital.education.coaching.ingestion.model.properties.DataImportPropertiesResponse;
import com.paytm.digital.education.coaching.ingestion.service.importdata.AbstractImportService;
import com.paytm.digital.education.coaching.ingestion.service.importdata.ImportService;
import com.paytm.digital.education.coaching.ingestion.transformer.importdata.ImportCoachingCTAMappingTransformer;
import com.paytm.digital.education.coaching.producer.controller.ProducerCoachingCourseController;
import com.paytm.digital.education.coaching.producer.model.dto.CoachingCourseDTO;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCoursePatchRequest;
import com.paytm.digital.education.utility.JsonUtils;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EMPTY_STRING;
import static com.paytm.digital.education.coaching.constants.GoogleSheetImportConstants.COACHING_CTA_MAPPING_SHEET_ID;

@Service
public class CoachingCTAMappingImportService extends AbstractImportService
        implements ImportService {

    private static final Logger log =
            LoggerFactory.getLogger(CoachingCTAMappingImportService.class);

    private static final String TYPE = "CoachingCTAMapping";

    @Autowired
    private ProducerCoachingCourseController producerCoachingCourseController;

    @Override
    public ImportResponse ingest() {

        final DataImportPropertiesResponse dataImportPropertiesResponse = this.getProperties(
                CoachingCourseForm.class,
                DataImportPropertiesRequest.builder()
                        .sheetIdKey(COACHING_CTA_MAPPING_SHEET_ID)
                        .build());

        final List<Object> ctaMappingFormData = this.getFormData(dataImportPropertiesResponse);

        final List<CoachingCTAMappingForm> failedCTAMappingFormList = this
                .getFailedData(TYPE, CoachingCTAMappingForm.class, COACHING);

        return this.processRecords(ctaMappingFormData, failedCTAMappingFormList,
                CoachingCTAMappingForm.class, TYPE);
    }

    @Override
    protected <T> void upsertNewRecords(final T form, final List<Object> failedDataList,
            final Class<T> clazz) {

        final CoachingCTAMappingForm ctaMappingForm = (CoachingCTAMappingForm) clazz.cast(form);
        ResponseEntity<CoachingCourseDTO> response = null;
        String failureMessage = EMPTY_STRING;
        CoachingCoursePatchRequest coachingCoursePatchRequest =
                ImportCoachingCTAMappingTransformer.convert(ctaMappingForm);
        try {
            response = this.producerCoachingCourseController
                    .patchCoachingProgram(coachingCoursePatchRequest);
        } catch (final Exception e) {
            log.error("Got Exception in upsertNewRecords for input: {}, exception: ", e, form);
            failureMessage = e.getMessage();
        }

        if (null == response || !response.getStatusCode().is2xxSuccessful()
                || null == response.getBody() || null == response.getBody().getCourseId()) {
            log.error("Response: {}", response);
            if (EMPTY_STRING.equals(failureMessage)) {
                failureMessage = "Failed to put new data in CoachingCourse collection";
            }

            this.addToFailedList(JsonUtils.toJson(form),
                    failureMessage, true, COACHING, TYPE, failedDataList);
        }
    }

    @Override
    protected <T> void upsertFailedRecords(T form, Class<T> clazz) {
        final CoachingCTAMappingForm ctaMappingForm = (CoachingCTAMappingForm) clazz.cast(form);
        try {
            CoachingCoursePatchRequest coachingCoursePatchRequest =
                    ImportCoachingCTAMappingTransformer.convert(ctaMappingForm);
            this.producerCoachingCourseController.patchCoachingProgram(coachingCoursePatchRequest);
        } catch (final Exception e) {
            log.error("Got Exception in upsertFailedRecords for input: {}, exception: ", e, form);
        }
    }
}
