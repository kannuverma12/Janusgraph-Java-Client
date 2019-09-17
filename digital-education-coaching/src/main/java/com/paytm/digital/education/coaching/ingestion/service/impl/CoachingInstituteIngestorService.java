package com.paytm.digital.education.coaching.ingestion.service.impl;

import com.paytm.digital.education.coaching.exeptions.CoachingBaseException;
import com.paytm.digital.education.coaching.ingestion.model.IngestorResponse;
import com.paytm.digital.education.coaching.ingestion.model.googleform.CoachingInstituteForm;
import com.paytm.digital.education.coaching.ingestion.model.properties.PropertiesRequest;
import com.paytm.digital.education.coaching.ingestion.model.properties.PropertiesResponse;
import com.paytm.digital.education.coaching.ingestion.service.IngestorService;
import com.paytm.digital.education.coaching.ingestion.service.IngestorServiceHelper;
import com.paytm.digital.education.coaching.ingestion.transformer.IngestorCoachingInstituteTransformer;
import com.paytm.digital.education.coaching.producer.controller.ProducerCoachingInstituteController;
import com.paytm.digital.education.coaching.producer.model.dto.CoachingInstituteDTO;
import com.paytm.digital.education.coaching.producer.model.embedded.KeyHighlight;
import com.paytm.digital.education.coaching.producer.model.request.CoachingInstituteDataRequest;
import com.paytm.digital.education.utility.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.paytm.digital.education.coaching.constants.CoachingConstants.COACHING;
import static com.paytm.digital.education.coaching.constants.CoachingConstants.EMPTY_STRING;
import static com.paytm.digital.education.coaching.constants.GoogleSheetConstants.COACHING_INSTITUTE_SHEET_HEADER_RANGE;
import static com.paytm.digital.education.coaching.constants.GoogleSheetConstants.COACHING_INSTITUTE_SHEET_ID;
import static com.paytm.digital.education.coaching.constants.GoogleSheetConstants.COACHING_INSTITUTE_SHEET_RANGE_TEMPLATE;
import static com.paytm.digital.education.coaching.constants.GoogleSheetConstants.COACHING_INSTITUTE_SHEET_START_ROW;

@Slf4j
@Service
public class CoachingInstituteIngestorService extends IngestorServiceHelper
        implements IngestorService {

    private static final String TYPE = "CoachingInstitute";

    @Value("${coaching.institute.logo.prefix}")
    protected String coachingInstituteLogoPrefix;
    @Value("${coaching.institute.image.prefix}")
    protected String coachingInstituteImagePrefix;
    @Value("${coaching.institute.highlightlogo.prefix}")
    protected String coachingInstituteHighlightLogoPrefix;

    @Autowired
    private ProducerCoachingInstituteController producerCoachingInstituteController;

    @Override
    public IngestorResponse ingest() {

        final PropertiesResponse propertiesResponse = this.getProperties(PropertiesRequest.builder()
                .sheetIdKey(COACHING_INSTITUTE_SHEET_ID)
                .sheetHeaderRangeKey(COACHING_INSTITUTE_SHEET_HEADER_RANGE)
                .sheetStartRowKey(COACHING_INSTITUTE_SHEET_START_ROW)
                .sheetRangeTemplateKey(COACHING_INSTITUTE_SHEET_RANGE_TEMPLATE)
                .build());

        final List<Object> instituteFormData = this.getFormData(propertiesResponse);

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
            log.error("Got Exception in upsertNewRecords for input: {}, exception: ", form, e);
            failureMessage = e.getMessage();
        }

        if (null == response || !response.getStatusCode().is2xxSuccessful()
                || null == response.getBody() || null == response.getBody().getInstituteId()) {
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
            log.error("Got Exception in upsertFailedRecords for input: {}, exception: ", form, e);
        }
    }

    private CoachingInstituteDataRequest buildRequest(final CoachingInstituteForm form)
            throws CoachingBaseException {
        final CoachingInstituteDataRequest request = IngestorCoachingInstituteTransformer
                .convert(form);
        request.setLogo(this.uploadFile(request.getLogo(), this.coachingInstituteLogoPrefix));
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
