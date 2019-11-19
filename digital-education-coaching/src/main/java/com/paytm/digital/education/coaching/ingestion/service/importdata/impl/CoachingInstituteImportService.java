package com.paytm.digital.education.coaching.ingestion.service.importdata.impl;

import com.paytm.digital.education.coaching.exeption.CoachingBaseException;
import com.paytm.digital.education.coaching.ingestion.model.ImportResponse;
import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingInstituteForm;
import com.paytm.digital.education.coaching.ingestion.model.properties.DataImportPropertiesRequest;
import com.paytm.digital.education.coaching.ingestion.model.properties.DataImportPropertiesResponse;
import com.paytm.digital.education.coaching.ingestion.service.importdata.AbstractImportService;
import com.paytm.digital.education.coaching.ingestion.service.importdata.ImportService;
import com.paytm.digital.education.coaching.ingestion.transformer.importdata.ImportCoachingInstituteTransformer;
import com.paytm.digital.education.coaching.producer.controller.ProducerCoachingInstituteController;
import com.paytm.digital.education.coaching.producer.model.dto.CoachingInstituteDTO;
import com.paytm.digital.education.coaching.producer.model.embedded.KeyHighlight;
import com.paytm.digital.education.coaching.producer.model.request.CoachingInstituteDataRequest;
import com.paytm.digital.education.utility.JsonUtils;
import com.paytm.education.logger.Logger;
import com.paytm.education.logger.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EMPTY_STRING;
import static com.paytm.digital.education.coaching.constants.GoogleSheetImportConstants.COACHING_INSTITUTE_SHEET_ID;

@Service
public class CoachingInstituteImportService extends AbstractImportService
        implements ImportService {

    private static final Logger log = LoggerFactory.getLogger(CoachingInstituteImportService.class);

    private static final String TYPE = "CoachingInstitute";

    @Value("${coaching.institute.logo.prefix}")
    protected String coachingInstituteLogoPrefix;
    @Value("${coaching.institute.image.prefix}")
    protected String coachingInstituteImagePrefix;
    @Value("${coaching.institute.highlightlogo.prefix}")
    protected String coachingInstituteHighlightLogoPrefix;
    @Value("${coaching.institute.brochure.prefix}")
    protected String coachingInstitueBrochurePrefix;

    @Autowired
    private ProducerCoachingInstituteController producerCoachingInstituteController;

    @Override
    public ImportResponse ingest() {

        final DataImportPropertiesResponse dataImportPropertiesResponse = this.getProperties(
                CoachingInstituteForm.class,
                DataImportPropertiesRequest.builder()
                        .sheetIdKey(COACHING_INSTITUTE_SHEET_ID)
                        .build());

        final List<Object> instituteFormData = this.getFormData(dataImportPropertiesResponse);

        final List<CoachingInstituteForm> failedInstituteFormList = this
                .getFailedData(TYPE, CoachingInstituteForm.class, COACHING);

        return this.processRecords(instituteFormData,
                failedInstituteFormList, CoachingInstituteForm.class, TYPE
        );
    }

    @Override
    protected <T> void upsertNewRecords(final T form, final List<Object> failedDataList,
            final Class<T> clazz) {
        final CoachingInstituteForm instituteForm = (CoachingInstituteForm) clazz.cast(form);

        ResponseEntity<CoachingInstituteDTO> response = null;
        String failureMessage = EMPTY_STRING;
        try {
            final CoachingInstituteDataRequest request = this.buildRequest(instituteForm);
            if (null == instituteForm.getInstituteId()) {
                response = this.producerCoachingInstituteController.createInstitute(request);
            } else {
                response = this.producerCoachingInstituteController.updateInstitute(request);
            }
        } catch (final Exception e) {
            log.error("Got Exception in upsertNewRecords for input: {}, exception: ", e, form);
            failureMessage = e.getMessage();
        }

        if (null == response || !response.getStatusCode().is2xxSuccessful()
                || null == response.getBody() || null == response.getBody().getInstituteId()) {
            log.error("Response: {}", response);
            if (EMPTY_STRING.equals(failureMessage)) {
                failureMessage = "Failed to put new data in CoachingInstitute collection";
            }
            this.addToFailedList(JsonUtils.toJson(form), failureMessage, true,
                    COACHING, TYPE, failedDataList);
        }
    }

    @Override
    protected <T> void upsertFailedRecords(T form, Class<T> clazz) {
        final CoachingInstituteForm instituteForm = (CoachingInstituteForm) clazz.cast(form);
        try {
            final CoachingInstituteDataRequest request = this.buildRequest(instituteForm);
            if (null == instituteForm.getInstituteId()) {
                this.producerCoachingInstituteController.createInstitute(request);
            } else {
                this.producerCoachingInstituteController.updateInstitute(request);
            }
        } catch (final Exception e) {
            log.error("Got Exception in upsertFailedRecords for input: {}, exception: ", e, form);
        }
    }

    private CoachingInstituteDataRequest buildRequest(final CoachingInstituteForm form)
            throws CoachingBaseException {
        final CoachingInstituteDataRequest request = ImportCoachingInstituteTransformer
                .convert(form);
        request.setLogo(this.uploadFile(request.getLogo(), this.coachingInstituteLogoPrefix));
        request.setBrochureUrl(this.uploadFile(request.getBrochureUrl(),
                this.coachingInstitueBrochurePrefix));
        request.setCoverImage(this.uploadFile(request.getCoverImage(),
                this.coachingInstituteImagePrefix));

        fillHighlightLogos(request);

        return request;
    }

    private void fillHighlightLogos(final CoachingInstituteDataRequest request)
            throws CoachingBaseException {
        final List<KeyHighlight> highlights = request.getHighlights();
        if (!CollectionUtils.isEmpty(highlights)) {
            for (final KeyHighlight highlight : highlights) {
                if (!StringUtils.isEmpty(highlight.getLogo())) {
                    highlight.setLogo(this.uploadFile(highlight.getLogo(),
                            this.coachingInstituteHighlightLogoPrefix));
                }
            }
        }
    }
}
