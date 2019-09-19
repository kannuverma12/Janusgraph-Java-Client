package com.paytm.digital.education.coaching.ingestion.service.importdata.impl;

import com.paytm.digital.education.coaching.ingestion.model.ImportResponse;
import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingExamForm;
import com.paytm.digital.education.coaching.ingestion.model.properties.DataImportPropertiesRequest;
import com.paytm.digital.education.coaching.ingestion.model.properties.DataImportPropertiesResponse;
import com.paytm.digital.education.coaching.ingestion.service.importdata.AbstractImportService;
import com.paytm.digital.education.coaching.ingestion.service.importdata.ImportService;
import com.paytm.digital.education.coaching.ingestion.transformer.importdata.ImportCoachingExamTransformer;
import com.paytm.digital.education.coaching.producer.controller.ProducerCoachingExamController;
import com.paytm.digital.education.coaching.producer.model.dto.CoachingExamDTO;
import com.paytm.digital.education.coaching.producer.model.request.CoachingExamDataRequest;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EMPTY_STRING;
import static com.paytm.digital.education.coaching.constants.GoogleSheetIngestionConstants.COACHING_EXAM_SHEET_HEADER_RANGE;
import static com.paytm.digital.education.coaching.constants.GoogleSheetIngestionConstants.COACHING_EXAM_SHEET_ID;
import static com.paytm.digital.education.coaching.constants.GoogleSheetIngestionConstants.COACHING_EXAM_SHEET_RANGE_TEMPLATE;
import static com.paytm.digital.education.coaching.constants.GoogleSheetIngestionConstants.COACHING_EXAM_SHEET_START_ROW;

@Slf4j
@Service
public class CoachingExamImportService extends AbstractImportService implements ImportService {

    private static final String TYPE = "CoachingExam";

    @Autowired
    private ProducerCoachingExamController producerCoachingExamController;

    @Override
    public ImportResponse ingest() {

        final DataImportPropertiesResponse dataImportPropertiesResponse = this.getProperties(
                DataImportPropertiesRequest.builder()
                        .sheetIdKey(COACHING_EXAM_SHEET_ID)
                        .sheetHeaderRangeKey(COACHING_EXAM_SHEET_HEADER_RANGE)
                        .sheetStartRowKey(COACHING_EXAM_SHEET_START_ROW)
                        .sheetRangeTemplateKey(COACHING_EXAM_SHEET_RANGE_TEMPLATE)
                        .build());

        final List<Object> examFormData = this.getFormData(dataImportPropertiesResponse);

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
            final CoachingExamDataRequest request = ImportCoachingExamTransformer.convert(
                    examForm);
            if (null == examForm.getCoachingExamId()) {
                response = this.producerCoachingExamController.insertCoachingExam(request);
            } else {
                response = this.producerCoachingExamController.updateCoachingExam(request);
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
    protected <T> void upsertFailedRecords(final T form, final Class<T> clazz) {
        final CoachingExamForm examForm = (CoachingExamForm) clazz.cast(form);
        try {

            final CoachingExamDataRequest request = ImportCoachingExamTransformer.convert(
                    examForm);
            if (null == examForm.getCoachingExamId()) {
                this.producerCoachingExamController.insertCoachingExam(request);
            } else {
                this.producerCoachingExamController.updateCoachingExam(request);
            }
        } catch (final Exception e) {
            log.error("Got Exception in upsertFailedRecords for input: {}, exception: ", form, e);
        }
    }
}
