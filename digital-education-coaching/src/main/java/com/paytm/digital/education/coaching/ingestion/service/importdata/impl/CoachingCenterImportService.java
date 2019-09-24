package com.paytm.digital.education.coaching.ingestion.service.importdata.impl;

import com.paytm.digital.education.coaching.exeptions.CoachingBaseException;
import com.paytm.digital.education.coaching.ingestion.model.ImportResponse;
import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingCenterForm;
import com.paytm.digital.education.coaching.ingestion.model.properties.DataImportPropertiesRequest;
import com.paytm.digital.education.coaching.ingestion.model.properties.DataImportPropertiesResponse;
import com.paytm.digital.education.coaching.ingestion.service.importdata.AbstractImportService;
import com.paytm.digital.education.coaching.ingestion.service.importdata.ImportService;
import com.paytm.digital.education.coaching.ingestion.transformer.importdata.ImportCoachingCenterTransformer;
import com.paytm.digital.education.coaching.producer.controller.ProducerCoachingCenterController;
import com.paytm.digital.education.coaching.producer.model.dto.CoachingCenterDTO;
import com.paytm.digital.education.coaching.producer.model.request.CoachingCenterDataRequest;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EMPTY_STRING;
import static com.paytm.digital.education.coaching.constants.GoogleSheetImportConstants.COACHING_CENTER_SHEET_ID;

@Slf4j
@Service
public class CoachingCenterImportService extends AbstractImportService
        implements ImportService {

    private static final String TYPE = "CoachingCenter";

    @Value("{$coaching.center.image.prefix}")
    private String coachingCenterImagePrefix;

    @Autowired
    private ProducerCoachingCenterController producerCoachingCenterController;

    @Override
    public ImportResponse ingest() {

        final DataImportPropertiesResponse dataImportPropertiesResponse = this.getProperties(
                CoachingCenterForm.class,
                DataImportPropertiesRequest.builder()
                        .sheetIdKey(COACHING_CENTER_SHEET_ID)
                        .build());

        final List<Object> centerFormData = this.getFormData(dataImportPropertiesResponse);

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
            final CoachingCenterDataRequest request = this.buildRequest(centerForm);
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
            log.error("Response: {}", response);
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
            final CoachingCenterDataRequest request = this.buildRequest(centerForm);
            if (null == centerForm.getCenterId()) {
                this.producerCoachingCenterController.insertCoachingCenter(request);
            } else {
                this.producerCoachingCenterController.updateCoachingCenter(request);
            }
        } catch (final Exception e) {
            log.error("Got Exception in upsertFailedRecords for input: {}, exception: ", form, e);
        }
    }

    private CoachingCenterDataRequest buildRequest(final CoachingCenterForm form)
            throws CoachingBaseException {
        final CoachingCenterDataRequest request = ImportCoachingCenterTransformer.convert(
                form);
        request.setCenterImage(this.uploadFile(request.getCenterImage(),
                this.coachingCenterImagePrefix));
        return request;
    }
}
