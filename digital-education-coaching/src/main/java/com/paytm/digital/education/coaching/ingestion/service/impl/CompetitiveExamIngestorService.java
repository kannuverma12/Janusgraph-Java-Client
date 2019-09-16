package com.paytm.digital.education.coaching.ingestion.service.impl;

import com.paytm.digital.education.coaching.ingestion.model.IngestorResponse;
import com.paytm.digital.education.coaching.ingestion.model.googleform.CompetitiveExamForm;
import com.paytm.digital.education.coaching.ingestion.model.properties.PropertiesRequest;
import com.paytm.digital.education.coaching.ingestion.model.properties.PropertiesResponse;
import com.paytm.digital.education.coaching.ingestion.service.IngestorService;
import com.paytm.digital.education.coaching.ingestion.service.IngestorServiceHelper;
import com.paytm.digital.education.coaching.ingestion.transformer.IngestorCompetitiveExamTransformer;
import com.paytm.digital.education.coaching.producer.controller.ProducerTargetExamController;
import com.paytm.digital.education.coaching.producer.model.dto.TargetExamDTO;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ProducerTargetExamController producerTargetExamController;

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
                this.producerTargetExamController.updateExam(
                        IngestorCompetitiveExamTransformer.convert(newCompetitiveExamForm));
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
                response = this.producerTargetExamController.updateExam(
                        IngestorCompetitiveExamTransformer.convert(newCompetitiveExamForm));
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
}
