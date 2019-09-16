package com.paytm.digital.education.coaching.ingestion.service.impl;

import com.paytm.digital.education.coaching.ingestion.model.IngestorResponse;
import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingCenterForm;
import com.paytm.digital.education.coaching.ingestion.model.properties.PropertiesRequest;
import com.paytm.digital.education.coaching.ingestion.model.properties.PropertiesResponse;
import com.paytm.digital.education.coaching.ingestion.service.IngestorService;
import com.paytm.digital.education.coaching.ingestion.service.IngestorServiceHelper;
import com.paytm.digital.education.coaching.ingestion.transformer.IngestorCoachingCenterTransformer;
import com.paytm.digital.education.coaching.producer.controller.ProducerCoachingCenterController;
import com.paytm.digital.education.coaching.producer.model.dto.CoachingCenterDTO;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCenterDataRequest;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EMPTY_STRING;
import static com.paytm.digital.education.coaching.constants.GoogleSheetConstants.COACHING_CENTER_SHEET_HEADER_RANGE;
import static com.paytm.digital.education.coaching.constants.GoogleSheetConstants.COACHING_CENTER_SHEET_ID;
import static com.paytm.digital.education.coaching.constants.GoogleSheetConstants.COACHING_CENTER_SHEET_RANGE_TEMPLATE;
import static com.paytm.digital.education.coaching.constants.GoogleSheetConstants.COACHING_CENTER_SHEET_START_ROW;

@Slf4j
@Service
public class CoachingCenterIngestorService extends IngestorServiceHelper
        implements IngestorService {

    private static final String TYPE = "CoachingCenter";

    @Autowired
    private ProducerCoachingCenterController producerCoachingCenterController;

    @Override
    public IngestorResponse ingest() {

        final PropertiesResponse propertiesResponse = this.getProperties(PropertiesRequest.builder()
                .sheetIdKey(COACHING_CENTER_SHEET_ID)
                .sheetHeaderRangeKey(COACHING_CENTER_SHEET_HEADER_RANGE)
                .sheetStartRowKey(COACHING_CENTER_SHEET_START_ROW)
                .sheetRangeTemplateKey(COACHING_CENTER_SHEET_RANGE_TEMPLATE)
                .build());

        final List<Object> centerFormData = this.getFormData(propertiesResponse);

        final List<CoachingCenterForm> failedCenterFormList = this
                .getFailedData(TYPE, CoachingCenterForm.class, COACHING);

        return this.processRecords(centerFormData,
                failedCenterFormList, CoachingCenterForm.class, TYPE
        );
    }

    @Override
    protected <T> void upsertNewRecords(final T form, final List<Object> failedDataList,
            final Class<T> clazz) {

        final CoachingCenterForm centerForm = (CoachingCenterForm) clazz.cast(form);

        ResponseEntity<CoachingCenterDTO> response = null;
        String failureMessage = EMPTY_STRING;
        try {
            final CoachingCenterDataRequest request = IngestorCoachingCenterTransformer.convert(
                    centerForm);
            if (null == centerForm.getCenterId()) {
                response = this.producerCoachingCenterController.insertCoachingCenter(request);
            } else {
                response = this.producerCoachingCenterController.updateCoachingCenter(request);
            }
        } catch (final Exception e) {
            log.error("Got Exception in upsertNewRecords for input: {}, exception: ", form, e);
            failureMessage = e.getMessage();
        }

        if (null == response || !response.getStatusCode().is2xxSuccessful()
                || null == response.getBody() || null == response.getBody().getCenterId()) {

            if (EMPTY_STRING.equals(failureMessage)) {
                failureMessage = "Failed to put new data in CoachingCenter collection";
            }

            this.addToFailedList(JsonUtils.toJson(form), failureMessage, true,
                    COACHING, TYPE, failedDataList);
        }
    }

    @Override
    protected <T> void upsertFailedRecords(T form, Class<T> clazz) {
        final CoachingCenterForm centerForm = (CoachingCenterForm) clazz.cast(form);
        try {
            final CoachingCenterDataRequest request = IngestorCoachingCenterTransformer.convert(
                    centerForm);
            if (null == centerForm.getCenterId()) {
                this.producerCoachingCenterController.insertCoachingCenter(request);
            } else {
                this.producerCoachingCenterController.updateCoachingCenter(request);
            }
        } catch (final Exception e) {
            log.error("Got Exception in upsertFailedRecords for input: {}, exception: ", form, e);
        }
    }
}
