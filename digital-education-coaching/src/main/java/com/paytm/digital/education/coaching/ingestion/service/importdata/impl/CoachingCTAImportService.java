package com.paytm.digital.education.coaching.ingestion.service.importdata.impl;

import com.paytm.digital.education.coaching.ingestion.model.ImportResponse;
import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingCTAForm;
import com.paytm.digital.education.coaching.ingestion.model.properties.DataImportPropertiesRequest;
import com.paytm.digital.education.coaching.ingestion.model.properties.DataImportPropertiesResponse;
import com.paytm.digital.education.coaching.ingestion.service.importdata.AbstractImportService;
import com.paytm.digital.education.coaching.ingestion.service.importdata.ImportService;
import com.paytm.digital.education.coaching.ingestion.transformer.importdata.ImportCoachingCTATransformer;
import com.paytm.digital.education.coaching.producer.model.dto.CoachingCtaDTO;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCtaDataRequest;
import com.paytm.digital.education.coaching.producer.service.CoachingCtaManagerService;
import com.paytm.digital.education.utility.JsonUtils;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EMPTY_STRING;
import static com.paytm.digital.education.coaching.constants.GoogleSheetImportConstants.COACHING_CTA_SHEET_ID;

@Service
public class CoachingCTAImportService extends AbstractImportService
        implements ImportService {

    private static final Logger log = LoggerFactory.getLogger(CoachingCTAImportService.class);

    private static final String TYPE = "CoachingCTA";

    @Autowired
    private CoachingCtaManagerService coachingCtaManagerService;

    @Override
    public ImportResponse ingest() {

        final DataImportPropertiesResponse dataImportPropertiesResponse = this.getProperties(
                CoachingCTAForm.class,
                DataImportPropertiesRequest.builder()
                        .sheetIdKey(COACHING_CTA_SHEET_ID)
                        .build());

        final List<Object> ctaFormData = this.getFormData(dataImportPropertiesResponse);

        final List<CoachingCTAForm> failedCTAFormList = this
                .getFailedData(TYPE, CoachingCTAForm.class, COACHING);

        return this.processRecords(ctaFormData, failedCTAFormList,
                CoachingCTAForm.class, TYPE);
    }

    @Override
    protected <T> void upsertNewRecords(final T form, final List<Object> failedDataList,
            final Class<T> clazz) {

        final CoachingCTAForm coachingCTAForm = (CoachingCTAForm) clazz.cast(form);
        CoachingCtaDTO response = null;
        String failureMessage = EMPTY_STRING;
        try {
            final CoachingCtaDataRequest coachingCtaDataRequest =
                    ImportCoachingCTATransformer.convert(
                            coachingCTAForm);
            if (null == coachingCtaDataRequest.getCtaId()) {
                response = this.coachingCtaManagerService.insertCoachingCta(coachingCtaDataRequest);
            } else {
                response = this.coachingCtaManagerService.updateCoachingCta(coachingCtaDataRequest);
            }
        } catch (final Exception e) {
            log.error("Got Exception in upsertNewRecords for input: {}, exception: ", e, form);
            failureMessage = e.getMessage();
        }

        if (null == response) {
            if (EMPTY_STRING.equals(failureMessage)) {
                failureMessage = "Failed to put new data in CoachingCTA collection";
            }
            this.addToFailedList(JsonUtils.toJson(form),
                    failureMessage, true, COACHING, TYPE, failedDataList);
        }
    }

    @Override
    protected <T> void upsertFailedRecords(T form, Class<T> clazz) {
        final CoachingCTAForm courseForm = (CoachingCTAForm) clazz.cast(form);
        try {
            final CoachingCtaDataRequest request = ImportCoachingCTATransformer.convert(
                    courseForm);
            if (null == courseForm.getCtaId()) {
                this.coachingCtaManagerService.insertCoachingCta(request);
            } else {
                this.coachingCtaManagerService.updateCoachingCta(request);
            }
        } catch (final Exception e) {
            log.error("Got Exception in upsertFailedRecords for input: {}, exception: ", e, form);
        }
    }
}
