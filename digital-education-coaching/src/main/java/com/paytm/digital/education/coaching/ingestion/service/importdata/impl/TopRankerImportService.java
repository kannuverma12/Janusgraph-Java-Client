package com.paytm.digital.education.coaching.ingestion.service.importdata.impl;

import com.paytm.digital.education.coaching.exeptions.CoachingBaseException;
import com.paytm.digital.education.coaching.ingestion.model.ImportResponse;
import com.paytm.digital.education.coaching.ingestion.model.googleform.TopRankerForm;
import com.paytm.digital.education.coaching.ingestion.model.properties.DataImportPropertiesRequest;
import com.paytm.digital.education.coaching.ingestion.model.properties.DataImportPropertiesResponse;
import com.paytm.digital.education.coaching.ingestion.service.importdata.AbstractImportService;
import com.paytm.digital.education.coaching.ingestion.service.importdata.ImportService;
import com.paytm.digital.education.coaching.ingestion.transformer.importdata.ImportTopRankerTransformer;
import com.paytm.digital.education.coaching.producer.controller.ProducerTopRankerController;
import com.paytm.digital.education.coaching.producer.model.dto.TopRankerDTO;
import com.paytm.digital.education.coaching.producer.model.request.TopRankerDataRequest;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EMPTY_STRING;
import static com.paytm.digital.education.coaching.constants.GoogleSheetIngestionConstants.TOP_RANKER_SHEET_HEADER_RANGE;
import static com.paytm.digital.education.coaching.constants.GoogleSheetIngestionConstants.TOP_RANKER_SHEET_ID;
import static com.paytm.digital.education.coaching.constants.GoogleSheetIngestionConstants.TOP_RANKER_SHEET_RANGE_TEMPLATE;
import static com.paytm.digital.education.coaching.constants.GoogleSheetIngestionConstants.TOP_RANKER_SHEET_START_ROW;

@Slf4j
@Service
public class TopRankerImportService extends AbstractImportService implements ImportService {

    private static final String TYPE = "CoachingTopRanker";

    @Value("${coaching.topranker.image.prefix}")
    protected String coachingTopRankerImagePrefix;

    @Autowired
    private ProducerTopRankerController producerTopRankerController;

    @Override
    public ImportResponse ingest() {

        final DataImportPropertiesResponse dataImportPropertiesResponse = this.getProperties(
                DataImportPropertiesRequest.builder()
                        .sheetIdKey(TOP_RANKER_SHEET_ID)
                        .sheetHeaderRangeKey(TOP_RANKER_SHEET_HEADER_RANGE)
                        .sheetStartRowKey(TOP_RANKER_SHEET_START_ROW)
                        .sheetRangeTemplateKey(TOP_RANKER_SHEET_RANGE_TEMPLATE)
                        .build());

        final List<Object> topRankerFormData = this.getFormData(dataImportPropertiesResponse);

        final List<TopRankerForm> failedTopRankerFormList = this
                .getFailedData(TYPE, TopRankerForm.class, COACHING);

        return this.processRecords(topRankerFormData, failedTopRankerFormList,
                TopRankerForm.class, TYPE);
    }

    @Override
    protected <T> void upsertNewRecords(final T form, final List<Object> failedDataList,
            final Class<T> clazz) {
        final TopRankerForm topRankerForm = (TopRankerForm) clazz.cast(form);

        ResponseEntity<TopRankerDTO> response = null;
        String failureMessage = EMPTY_STRING;
        try {
            final TopRankerDataRequest request = this.buildRequest(topRankerForm);
            if (null == topRankerForm.getTopRankerId()) {
                response = this.producerTopRankerController.createTopRanker(request);
            } else {
                response = this.producerTopRankerController.updateTopRanker(request);
            }
        } catch (final Exception e) {
            log.error("Got Exception in upsertNewRecords for input: {}, exception: ", form, e);
            failureMessage = e.getMessage();
        }

        if (null == response || !response.getStatusCode().is2xxSuccessful()
                || null == response.getBody() || null == response.getBody().getTopRankerId()) {
            log.error("Response: {}", response);
            if (EMPTY_STRING.equals(failureMessage)) {
                failureMessage = "Failed to put new data in TopRanker collection";
            }
            this.addToFailedList(JsonUtils.toJson(form),
                    failureMessage, true, COACHING, TYPE, failedDataList);
        }
    }

    @Override
    protected <T> void upsertFailedRecords(final T form, final Class<T> clazz) {
        final TopRankerForm topRankerForm = (TopRankerForm) clazz.cast(form);
        try {
            final TopRankerDataRequest request = this.buildRequest(topRankerForm);
            if (null == topRankerForm.getTopRankerId()) {
                this.producerTopRankerController.createTopRanker(request);
            } else {
                this.producerTopRankerController.updateTopRanker(request);
            }
        } catch (final Exception e) {
            log.error("Got Exception in upsertFailedRecords for input: {}, exception: ", form, e);
        }
    }

    private TopRankerDataRequest buildRequest(final TopRankerForm form)
            throws CoachingBaseException {
        final TopRankerDataRequest request = ImportTopRankerTransformer.convert(form);
        request.setStudentPhoto(this.uploadFile(request.getStudentPhoto(),
                this.coachingTopRankerImagePrefix));
        return request;
    }
}
