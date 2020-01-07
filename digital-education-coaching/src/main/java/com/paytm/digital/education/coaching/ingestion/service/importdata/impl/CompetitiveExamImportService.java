package com.paytm.digital.education.coaching.ingestion.service.importdata.impl;

import com.paytm.digital.education.coaching.ingestion.model.ImportResponse;
import com.paytm.digital.education.coaching.ingestion.model.googleform.CompetitiveExamForm;
import com.paytm.digital.education.coaching.ingestion.model.properties.DataImportPropertiesRequest;
import com.paytm.digital.education.coaching.ingestion.model.properties.DataImportPropertiesResponse;
import com.paytm.digital.education.coaching.ingestion.service.importdata.AbstractImportService;
import com.paytm.digital.education.coaching.ingestion.service.importdata.ImportService;
import com.paytm.digital.education.coaching.ingestion.transformer.importdata.ImportCompetitiveExamTransformer;
import com.paytm.digital.education.coaching.producer.controller.ProducerTargetExamController;
import com.paytm.digital.education.coaching.producer.model.dto.TargetExamDTO;
import com.paytm.digital.education.utility.JsonUtils;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EMPTY_STRING;
import static com.paytm.digital.education.coaching.constants.GoogleSheetImportConstants.COMPETITIVE_EXAM_SHEET_ID;

@Service
public class CompetitiveExamImportService extends AbstractImportService
        implements ImportService {

    private static final Logger log = LoggerFactory.getLogger(CompetitiveExamImportService.class);

    private static final String TYPE = "CompetitiveExam";

    @Autowired
    private ProducerTargetExamController producerTargetExamController;

    @Override
    public ImportResponse ingest() {

        final DataImportPropertiesResponse dataImportPropertiesResponse = this.getProperties(
                CompetitiveExamForm.class,
                DataImportPropertiesRequest.builder()
                        .sheetIdKey(COMPETITIVE_EXAM_SHEET_ID)
                        .build());

        final List<Object> competitiveExamFormData = this.getFormData(
                dataImportPropertiesResponse);

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
                this.producerTargetExamController.updateExam(
                        ImportCompetitiveExamTransformer.convert(newCompetitiveExamForm));
            }
        } catch (final Exception e) {
            log.error("Got Exception in upsertFailedRecords for input: {}, exception: ", e, form);
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
                response = this.producerTargetExamController.updateExam(
                        ImportCompetitiveExamTransformer.convert(newCompetitiveExamForm));
            }
        } catch (final Exception e) {
            log.error("Got Exception in upsertNewRecords for input: {}, exception: ", e, form);
            failureMessage = e.getMessage();
        }

        if (null == response || !response.getStatusCode().is2xxSuccessful()
                || null == response.getBody() || null == response.getBody().getExamId()) {
            log.error("Response: {}", response);
            if (EMPTY_STRING.equals(failureMessage)) {
                failureMessage = "Failed to put data in CompetitiveExam collection";
            }

            this.addToFailedList(JsonUtils.toJson(form),
                    failureMessage, true, COACHING, TYPE, failedDataList);
        }
    }
}
